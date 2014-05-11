package jp.co.prospire.techdemo.opencv.dao;

import java.sql.Timestamp;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface EncodedImageDAO
{
    @SqlUpdate("create table encoded_images (id BIGSERIAL primary key, short_url varchar(64), encoded_image text, create_date timestamp)")
    void createEncodedImagesTable();

    @SqlUpdate("insert into encoded_images (id, short_url, encoded_image, create_date) values (:id, :short_url, :encoded_image, :create_date)")
    void insert(
            @Bind("id") long id,
            @Bind("short_url") String shortUrl,
            @Bind("encoded_image") String encodedImage,
            @Bind("create_date") Timestamp createDate
        );

    @SqlQuery("select id from encoded_images where short_url = :short_url")
    long getIdByShortUrl(@Bind("short_url") String shortUrl);

    @SqlQuery("select encoded_image from encoded_images where id = :id")
    String getEncodedImageById(@Bind("id") long id);
    
    void close();
}
