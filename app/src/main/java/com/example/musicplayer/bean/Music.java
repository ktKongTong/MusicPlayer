package com.example.musicplayer.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.ByteArrayOutputStream;

public class Music implements Parcelable {
    private String name;
    private byte[] coverImage;
    private Type type;
    private String album;
    private String artist;
    private String Duration;
    private String filePath;

    protected Music(Parcel in) {
        name = in.readString();
        type = in.readParcelable(Type.class.getClassLoader());
        album = in.readString();
        artist = in.readString();
        Duration = in.readString();
        filePath = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(type, i);
        parcel.writeString(album);
        parcel.writeString(artist);
        parcel.writeString(Duration);
        parcel.writeString(filePath);
    }


    private enum Type implements Parcelable{
        FLAC,MP3;
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<Type> CREATOR = new Creator<Type>() {
            @Override
            public Type createFromParcel(Parcel in) {
                return  Type.values()[in.readInt()];
            }

            @Override
            public Type[] newArray(int size) {
                return new Type[size];
            }
        };
//        FLAC(1, "无损"),
//        MP3(2,"MP3");
//        private String type;
//        private int code;
//        Type(int code, String value) {
//            this.code = code;
//            this.type = value;
//        }
//        public String getType() {
//            return type;
//        }
//        public int getCode() {
//            return code;
//        }
    }



    public Music(String filepath,String filename) {
        this.setFilePath(filepath+filename);
        if(filename.endsWith(".mp3")){
            this.setType(Type.MP3);
        }else if(filename.endsWith(".flac")){
            this.setType(Type.FLAC);
        }
        if(this.getType() == Type.MP3){
            this.setName(filename.substring(0,filename.length()-4));
        }else if(this.getType() == Type.FLAC){
            this.setName(filename.substring(0,filename.length()-5));
        }
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(filepath+filename);
        byte[] embeddedPicture = metadataRetriever.getEmbeddedPicture();
        if(embeddedPicture != null){
            this.setCoverImage(BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length));
        }else {
            this.setCoverImage((Bitmap) null);
        }
        this.setAlbum(metadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM));
        this.setArtist(metadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST));
        this.setDuration(metadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getCoverImage() {
        return BitmapFactory.decodeByteArray(coverImage, 0, coverImage.length);
    }

    public void setCoverImage(Bitmap coverImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        coverImage.compress(Bitmap.CompressFormat.JPEG, 0, baos);//压缩位图
        this.coverImage=baos.toByteArray();
    }
    public void setCoverImage(byte[] coverImage) {
        this.coverImage=coverImage;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }
}
