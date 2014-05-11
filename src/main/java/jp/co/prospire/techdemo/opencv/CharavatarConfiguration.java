package jp.co.prospire.techdemo.opencv;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class CharavatarConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String template;

    @NotEmpty
    @JsonProperty
    private String canvasWidth = "480";

    @NotEmpty
    @JsonProperty
    private String canvasHeight = "480";

    @NotEmpty
    @JsonProperty
    private String smoothness = "4";

    @NotEmpty
    @JsonProperty
    private String consumerKey;

    @NotEmpty
    @JsonProperty
    private String consumerSecret;

    @NotEmpty
    @JsonProperty
    private String callbackUrl;

    @NotEmpty
    @JsonProperty
    private String temporaryDirectory;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty
    public String getTemplate()
    {
        return this.template;
    }
    
    @JsonProperty
    public void setTemplate(String template)
    {
        this.template = template;
    }

    @JsonProperty
    public String getCanvasWidth() {
        return this.canvasWidth;
    }
    
    @JsonProperty
    public String getCanvasHeight() {
        return this.canvasHeight;
    }
    
    @JsonProperty
    public String getSmoothness() {
        return this.smoothness;
    }

    @JsonProperty
    public String getConsumerKey() {
        return this.consumerKey;
    }
    
    @JsonProperty
    public String getConsumerSecret() {
        return this.consumerSecret;
    }

    @JsonProperty
    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    @JsonProperty
    public String getTemporaryDirectory() {
        return this.temporaryDirectory;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}