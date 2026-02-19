//package util;
//
//import entity.*;
//import model.*;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class MappingUtil {
//
//    public Artist toDto(ArtistEntity entity) {
//        Artist dto = new Artist();
//        dto.setName(entity.getName());
//        dto.setGenre(entity.getGenre());
//
//        if (entity.getAlbums() != null) {
//            List<Album> albums = new ArrayList<>();
//            for (AlbumEntity albumEntity : entity.getAlbums()) {
//                albums.add(toAlbumDto(albumEntity, false)); // false - не включать треки в альбомы
//            }
//            dto.setAlbums(albums);
//        }
//
//        if (entity.getTracks() != null) {
//            List<Track> tracks = new ArrayList<>();
//            for (TrackEntity trackEntity : entity.getTracks()) {
//                tracks.add(toTrackDto(trackEntity, false)); // false - не включать полную информацию об артисте и альбоме
//            }
//            dto.setTracks(tracks);
//        }
//
//        return dto;
//    }
//
//    public ArtistEntity toEntity(Artist dto) {
//        ArtistEntity entity = new ArtistEntity();
//        entity.setName(dto.getName());
//        entity.setGenre(dto.getGenre());
//        return entity;
//    }
//
//    public Album toDto(AlbumEntity entity) {
//        return toAlbumDto(entity, true); // true - включать треки
//    }
//
//    private Album toAlbumDto(AlbumEntity entity, boolean includeTracks) {
//        Album dto = new Album();
//        dto.setName(entity.getName());
//        dto.setYear(entity.getYear());
//
//        if (entity.getArtist() != null) {
//            Artist artistDto = new Artist();
//            artistDto.setName(entity.getArtist().getName());
//            artistDto.setGenre(entity.getArtist().getGenre());
//            dto.setArtist(artistDto);
//        }
//
//        if (includeTracks && entity.getTracks() != null && !entity.getTracks().isEmpty()) {
//            List<Track> trackDtos = entity.getTracks().stream()
//                    .map(trackEntity -> {
//                        Track trackDto = new Track();
//                        trackDto.setName(trackEntity.getName());
//                        trackDto.setDuration(trackEntity.getDuration());
//
//                        if (trackEntity.getArtist() != null) {
//                            Artist trackArtist = new Artist();
//                            trackArtist.setName(trackEntity.getArtist().getName());
//                            trackArtist.setGenre(trackEntity.getArtist().getGenre());
//                            trackDto.setArtist(trackArtist);
//                        }
//
//                        return trackDto;
//                    })
//                    .collect(Collectors.toList());
//            dto.setTracks(trackDtos);
//        }
//
//        return dto;
//    }
//
//
//    public AlbumEntity toEntity(Album dto) {
//        AlbumEntity entity = new AlbumEntity();
//        entity.setName(dto.getName());
//        entity.setYear(dto.getYear());
//        return entity;
//    }
//
//    public Track toDto(TrackEntity entity) {
//        return toTrackDto(entity, true);
//    }
//
//    private Track toTrackDto(TrackEntity entity, boolean includeFullInfo) {
//        Track dto = new Track();
//        dto.setName(entity.getName());
//        dto.setDuration(entity.getDuration());
//
//        if (entity.getArtist() != null) {
//            Artist artistDto = new Artist();
//            artistDto.setName(entity.getArtist().getName());
//            artistDto.setGenre(entity.getArtist().getGenre());
//            dto.setArtist(artistDto);
//        }
//
//        if (includeFullInfo && entity.getAlbum() != null) {
//            Album albumDto = new Album();
//            albumDto.setName(entity.getAlbum().getName());
//            albumDto.setYear(entity.getAlbum().getYear());
//            if (entity.getAlbum().getArtist() != null) {
//                Artist albumArtistDto = new Artist();
//                albumArtistDto.setName(entity.getAlbum().getArtist().getName());
//                albumArtistDto.setGenre(entity.getAlbum().getArtist().getGenre());
//                albumDto.setArtist(albumArtistDto);
//            }
//            dto.setAlbum(albumDto);
//        }
//
//        return dto;
//    }
//
//    public TrackEntity toEntity(Track dto) {
//        TrackEntity entity = new TrackEntity();
//        entity.setName(dto.getName());
//        entity.setDuration(dto.getDuration());
//        return entity;
//    }
//
//    public User toDto(UserEntity entity) {
//        User dto = new User();
//        dto.setName(entity.getName());
//        dto.setEmail(entity.getEmail());
//
//        if (entity.getPlaylist() != null) {
//            List<String> playlists = entity.getPlaylist().stream()
//                    .map(PlaylistEntity::getName)
//                    .collect(Collectors.toList());
//            dto.setPlaylists(playlists);
//        }
//
//        return dto;
//    }
//
//    public UserEntity toEntity(User dto) {
//        UserEntity entity = new UserEntity();
//        entity.setName(dto.getName());
//        entity.setEmail(dto.getEmail());
//        return entity;
//    }
//
//    public Playlist toDto(PlaylistEntity entity) {
//        Playlist dto = new Playlist();
//        dto.setName(entity.getName());
//
//        if (entity.getUser() != null) {
//            User userDto = new User();
//            userDto.setName(entity.getUser().getName());
//            userDto.setEmail(entity.getUser().getEmail());
//            dto.setUser(userDto);
//        }
//
//        return dto;
//    }
//
//    public PlaylistEntity toEntity(Playlist dto) {
//        PlaylistEntity entity = new PlaylistEntity();
//        entity.setName(dto.getName());
//        return entity;
//    }
//}