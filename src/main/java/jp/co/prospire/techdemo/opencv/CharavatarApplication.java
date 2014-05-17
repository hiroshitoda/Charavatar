package jp.co.prospire.techdemo.opencv;

import org.skife.jdbi.v2.DBI;

import jp.co.prospire.techdemo.opencv.core.HealthCheck.TemplateHealthCheck;
import jp.co.prospire.techdemo.opencv.dao.EncodedImageDAO;
import jp.co.prospire.techdemo.opencv.resources.ContoursListResource;
import jp.co.prospire.techdemo.opencv.resources.EncodedImageResource;
import jp.co.prospire.techdemo.opencv.resources.TweetResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class CharavatarApplication extends Application<CharavatarConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        new CharavatarApplication().run(args);
    }

    @Override
    public String getName()
    {
        return "Charavatar";
    }
    
    @Override
    public void initialize(Bootstrap<CharavatarConfiguration> bootstrap)
    {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new MigrationsBundle<CharavatarConfiguration>()
                {
                    @Override
                    public DataSourceFactory getDataSourceFactory(CharavatarConfiguration configuration)
                    {
                        return configuration.getDataSourceFactory();
                    }
                }
            );
    }

    @Override
    public void run(CharavatarConfiguration configuration,
                    Environment environment) throws ClassNotFoundException
    {
        environment.jersey().setUrlPattern("/service/*");

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(
                environment,
                configuration.getDataSourceFactory(),
                "postgresql"
            );
        final EncodedImageDAO encodedImageDAO = jdbi.onDemand(EncodedImageDAO.class);
        final EncodedImageResource encodedImageResource = new EncodedImageResource(
                encodedImageDAO,
                configuration.getTemporaryDirectory(),
                configuration.getCallbackUrl()
            );
        environment.jersey().register(encodedImageResource);
        
        final ContoursListResource contoursListResource = new ContoursListResource(
                Integer.parseInt(configuration.getCanvasWidth()),
                Integer.parseInt(configuration.getCanvasHeight()),
                Integer.parseInt(configuration.getSmoothness()),
                configuration.getTemporaryDirectory()
            );
        
        final TweetResource tweetResource = new TweetResource(
                configuration.getConsumerKey(),
                configuration.getConsumerSecret(),
                configuration.getCallbackUrl()
            );
        
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

        environment.jersey().register(contoursListResource);
        environment.jersey().register(tweetResource);
    }
}