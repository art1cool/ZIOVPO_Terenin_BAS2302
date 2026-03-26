package service;

import controller.GlobalExceptionHandler;
import entity.*;
import enums.*;
import lombok.RequiredArgsConstructor;
import model.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.LicenseHistoryRepository;
import repository.LicenseRepository;
import repository.UserRepository;
import repository.DeviceRepository;
import repository.DeviceLicenseRepository;
import util.MappingUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final LicenseHistoryRepository historyRepository;
    private final ProductService productService;
    private final LicenseTypeService licenseTypeService;
    private final UserRepository userRepository;
    private final MappingUtil mappingUtil;
    private final DeviceRepository deviceRepository;
    private final DeviceLicenseRepository deviceLicenseRepository;
    private final SigningService signingService;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Current user not found"));
    }

    private String generateLicenseCode() {
        return "LIC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public LicenseResponse createLicense(CreateLicenseRequest request) {
        ProductEntity product = productService.getProductOrFail(request.getProductId());
        LicenseTypeEntity licenseType = licenseTypeService.getTypeOrFail(request.getTypeId());
        UserEntity owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Owner user not found with id: " + request.getOwnerId()));

        LicenseEntity license = new LicenseEntity();
        license.setCode(generateLicenseCode());
        license.setProduct(product);
        license.setLicenseType(licenseType);
        license.setOwner(owner);
        license.setCreatedAt(LocalDateTime.now());
        license.setExpiresAt(null);
        license.setBlocked(false);
        license.setDeviceCount(5);
        license.setFirstActivationDate(null);
        license.setDescription(null);

        LicenseEntity savedLicense = licenseRepository.save(license);

        LicenseHistoryEntity history = new LicenseHistoryEntity();
        history.setLicense(savedLicense);
        history.setStatus(LicenseStatus.CREATED);
        history.setDescription("License created");
        history.setUser(getCurrentUser());
        history.setTimestamp(LocalDateTime.now());
        historyRepository.save(history);

        return mappingUtil.toDto(savedLicense);
    }

    @Transactional
    public TicketResponse activateLicense(ActivateLicenseRequest request) {
        UserEntity currentUser = getCurrentUser();

        LicenseEntity license = licenseRepository.findByCode(request.getActivationKey())
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("License not found with code: " + request.getActivationKey()));

        if (license.isBlocked()) {
            throw new GlobalExceptionHandler.ConflictException("License is blocked");
        }
        if (license.getExpiresAt() != null && license.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GlobalExceptionHandler.ConflictException("License has expired");
        }

        if (license.getUser() != null && !license.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("License is already activated by another user");
        }

        DeviceEntity device = deviceRepository.findByDeviceIdentifier(request.getDeviceIdentifier())
                .orElseGet(() -> {
                    DeviceEntity newDevice = new DeviceEntity();
                    newDevice.setDeviceIdentifier(request.getDeviceIdentifier());
                    newDevice.setName(request.getDeviceName() != null ? request.getDeviceName() : "Unknown Device");
                    newDevice.setUser(currentUser);
                    return deviceRepository.save(newDevice);
                });

        boolean alreadyActive = deviceLicenseRepository
                .findByLicenseAndDeviceAndStatus(license, device, DeviceLicenseStatus.ACTIVE)
                .isPresent();
        if (alreadyActive) {
            return buildTicketResponse(license, device);
        }

        long activeDevices = deviceLicenseRepository.countByLicenseAndStatus(license, DeviceLicenseStatus.ACTIVE);
        if (activeDevices >= license.getDeviceCount()) {
            throw new GlobalExceptionHandler.ConflictException("Maximum number of devices (" + license.getDeviceCount() + ") reached for this license");
        }

        boolean isFirstActivation = (license.getUser() == null);
        if (isFirstActivation) {
            license.setUser(currentUser);
            LocalDateTime now = LocalDateTime.now();
            license.setFirstActivationDate(now);

            Integer durationDays = license.getLicenseType().getDurationDays();
            if (durationDays != null) {
                license.setExpiresAt(now.plusDays(durationDays));
            }
            licenseRepository.save(license);
        }

        DeviceLicenseEntity deviceLicense = new DeviceLicenseEntity();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setUser(currentUser);
        deviceLicense.setStatus(DeviceLicenseStatus.ACTIVE);
        deviceLicense.setChangeDate(LocalDateTime.now());
        deviceLicense.setDescription("Device activated");
        deviceLicenseRepository.save(deviceLicense);

        LicenseHistoryEntity history = new LicenseHistoryEntity();
        history.setLicense(license);
        history.setStatus(isFirstActivation ? LicenseStatus.ACTIVATED : LicenseStatus.DEVICE_ADDED);
        history.setDescription(isFirstActivation ? "License activated" : "Device added to license");
        history.setUser(currentUser);
        history.setTimestamp(LocalDateTime.now());
        historyRepository.save(history);

        return buildTicketResponse(license, device);
    }

    private static final long TICKET_LIFETIME_SECONDS = 172800;

    private Ticket buildTicket(LicenseEntity license, DeviceEntity device) {
        Ticket ticket = new Ticket();
        ticket.setServerTime(LocalDateTime.now());
        ticket.setTicketLifetimeSeconds(TICKET_LIFETIME_SECONDS);
        ticket.setActivationDate(license.getFirstActivationDate());
        ticket.setExpiresAt(license.getExpiresAt());
        ticket.setUserId(license.getUser() != null ? license.getUser().getId() : null);
        ticket.setDeviceIdentifier(device.getDeviceIdentifier());
        ticket.setBlocked(license.isBlocked());
        ticket.setLicenseId(license.getId());
        ticket.setProductName(license.getProduct().getName());

        return ticket;
    }

    private TicketResponse buildTicketResponse(LicenseEntity license, DeviceEntity device) {
        Ticket ticket = buildTicket(license, device);
        String signature = signingService.sign(ticket);
        TicketResponse response = new TicketResponse();
        response.setTicket(ticket);
        response.setSignature(signature);
        return response;
    }

    @Transactional(readOnly = true)
    public TicketResponse verifyLicense(String licenseCode, String deviceIdentifier) {
        LicenseEntity license = licenseRepository.findByCode(licenseCode)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("License not found with code: " + licenseCode));

        if (license.isBlocked()) {
            throw new GlobalExceptionHandler.ConflictException("License is blocked");
        }

        if (license.getExpiresAt() != null && license.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GlobalExceptionHandler.ConflictException("License has expired");
        }

        DeviceEntity device = deviceRepository.findByDeviceIdentifier(deviceIdentifier)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Device not found with identifier: " + deviceIdentifier));

        boolean deviceActive = deviceLicenseRepository
                .findByLicenseAndDeviceAndStatus(license, device, DeviceLicenseStatus.ACTIVE)
                .isPresent();
        if (!deviceActive) {
            throw new GlobalExceptionHandler.ConflictException("Device is not activated for this license");
        }

        return buildTicketResponse(license, device);
    }

    @Transactional
    public TicketResponse renewLicense(RenewLicenseRequest request) {
        UserEntity currentUser = getCurrentUser();

        LicenseEntity license = licenseRepository.findByCode(request.getActivationKey())
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("License not found with code: " + request.getActivationKey()));

        if (license.getUser() == null || !license.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this license");
        }

        if (license.isBlocked()) {
            throw new GlobalExceptionHandler.ConflictException("License is blocked");
        }

        LocalDateTime now = LocalDateTime.now();
        if (license.getExpiresAt() == null) {
            throw new GlobalExceptionHandler.InvalidRequestException("Perpetual license cannot be renewed");
        }
        boolean isExpiringSoon = license.getExpiresAt().isBefore(now.plusDays(7));
        boolean isExpired = license.getExpiresAt().isBefore(now);
        if (!isExpired && !isExpiringSoon) {
            throw new GlobalExceptionHandler.ConflictException("License is not yet due for renewal (expires more than 7 days from now)");
        }

        Integer durationDays = license.getLicenseType().getDurationDays();
        if (durationDays == null) {
            throw new GlobalExceptionHandler.ConflictException("License type has no duration defined – cannot renew");
        }

        LocalDateTime newExpiresAt;
        if (isExpired) {
            newExpiresAt = now.plusDays(durationDays);
        } else {
            newExpiresAt = license.getExpiresAt().plusDays(durationDays);
        }
        license.setExpiresAt(newExpiresAt);

        licenseRepository.save(license);

        LicenseHistoryEntity history = new LicenseHistoryEntity();
        history.setLicense(license);
        history.setStatus(LicenseStatus.RENEWED);
        history.setDescription("License renewed, new expiration: " + newExpiresAt);
        history.setUser(currentUser);
        history.setTimestamp(now);
        historyRepository.save(history);

        DeviceEntity anyDevice = deviceLicenseRepository
                .findFirstByLicenseAndStatus(license, DeviceLicenseStatus.ACTIVE)
                .map(DeviceLicenseEntity::getDevice)
                .orElseThrow(() -> new GlobalExceptionHandler.ConflictException("No active device found for this license – cannot build ticket"));

        return buildTicketResponse(license, anyDevice);
    }
}