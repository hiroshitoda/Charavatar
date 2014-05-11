package jp.co.prospire.techdemo.opencv.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.base.Charsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import jp.co.prospire.techdemo.opencv.service.RedirectToTwitterView;
import jp.co.prospire.techdemo.opencv.service.SVGCanvasView;

@Path("/tweets")
@Produces(MediaType.TEXT_HTML)
public class TweetResource
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicLong counter;
    
    private final OAuthConsumer consumer;
    private final String consumerKey;
    private final String consumerSecret;
    private final OAuthProvider provider;
    private final String requestTokenUrl = "https://api.twitter.com/oauth/request_token";
    private final String authorizeUrl = "https://api.twitter.com/oauth/authorize";
    private final String accessTokenUrl = "https://api.twitter.com/oauth/access_token";
    private final String callbackUrl;
    private final String apiUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    
    private final HashMap<String, Integer> emptyList = new HashMap<String, Integer>();
    
    public TweetResource(String consumerKey, String consumerSecret,
            String callbackUrl)
    {
        this.counter = new AtomicLong();
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.consumer = new DefaultOAuthConsumer(
                this.consumerKey,
                this.consumerSecret
            );
        this.provider = new DefaultOAuthProvider(
                this.requestTokenUrl,
                this.accessTokenUrl,
                this.authorizeUrl
            );
        this.callbackUrl = callbackUrl;
        
        log(LogLevel.INFO, "consumer key:%s, consumer secret:%s",
                this.consumerKey,
                this.consumerSecret
            );
        log(LogLevel.INFO, "request token URL:%s, access token URL:%s, authorize URL:%s",
                this.requestTokenUrl,
                this.accessTokenUrl,
                this.authorizeUrl
            );
        log(LogLevel.INFO, "callback URL:%s",
                this.callbackUrl
            );
    }
    
    public enum LogLevel
    {
        ERROR,
        WARN,
        INFO,
        DEBUG
    }

    public void log(LogLevel loglevel, String formatString, Object... args)
    {
        long _counter = this.counter.get();

        Object[] _args = new Object[args.length + 1];
        _args[0] = _counter;
        for (int index = 0; index < args.length; index++)
        {
            _args[index + 1] = args[index];
        }

        String _formatString = String.format(
                "%d: " + formatString,
                _args
                );
        switch (loglevel)
        {
            case ERROR:
                logger.error(_formatString);
                break;
            case WARN:
                logger.warn(_formatString);
                break;
            case INFO:
                logger.info(_formatString);
                break;
            case DEBUG:
            default:
                logger.debug(_formatString);
        }
    }

    @GET
    @Timed
    @Path("/authUrl")
    public RedirectToTwitterView getAuthenticationUrl()
    {
        this.counter.incrementAndGet();
        
        String authUrl = "";
        try
        {
            authUrl = this.provider.retrieveRequestToken(
                    this.consumer,
                    this.callbackUrl
                );
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "can't get auth URL: %s",
                    e.getMessage()
                );
            return new RedirectToTwitterView(null, Charsets.UTF_8);
        }

        log(LogLevel.DEBUG, "auth URL: %s", authUrl);
        return new RedirectToTwitterView(authUrl, Charsets.UTF_8);
    }
    
    @GET
    @Timed
    @Path("/words")
    public SVGCanvasView getTweets(
            @QueryParam("oauth_token") String oauthToken,
            @QueryParam("oauth_verifier") String oauthVerifier
        )
    {
        log(LogLevel.DEBUG, "OAuth Token:%s, OAuth verifier:%s",
                oauthToken,
                oauthVerifier
                );
        try
        {
            provider.retrieveAccessToken(
                    this.consumer,
                    oauthVerifier
                );
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "can't get access tokens: %s",
                    e.getMessage()
                );
            return new SVGCanvasView(this.emptyList, Charsets.UTF_8);
        }

        String accessToken = this.consumer.getToken();
        String tokenSecret = this.consumer.getTokenSecret();
        log(LogLevel.DEBUG, "access token:%s, token secret:%s",
                accessToken,
                tokenSecret
                );

        OAuthConsumer newConsumer = new DefaultOAuthConsumer(
                this.consumerKey,
                this.consumerSecret
            );
        newConsumer.setTokenWithSecret(accessToken, tokenSecret);
        
        String jsonString = "";
        try
        {
            URL url = new URL(
                    this.apiUrl
                    + "?count=100"
                );
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setInstanceFollowRedirects(false);
            request.setConnectTimeout(10000);
            request.setReadTimeout(10000);
            newConsumer.sign(request);
            request.connect();
            
            if (request.getResponseCode() >= 400)
            {
                throw new Exception("error response code: " + request.getResponseCode());
            }
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(request.getInputStream())
                );
            while (true)
            {
                String line = reader.readLine();

                if (line == null)
                {
                    break;
                }

                jsonString += line;
            }
            reader.close();

            request.disconnect();
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "can't connect to %s: %s",
                    this.apiUrl,
                    e.getMessage()
                );
            return new SVGCanvasView(this.emptyList, Charsets.UTF_8);
        }
        
        HashMap<String, Integer> tweetWords = new HashMap<String, Integer>();
        try
        {
            // parse JSON to POJO.
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(jsonString);
            Tokenizer tokenizer = Tokenizer.builder().build();
          
            while (parser.nextToken() != null)
            {
                String elementName = parser.getCurrentName();
                if ("text".equals(elementName))
                {
                    parser.nextToken();
                    String tweetText = parser.getText();
                    log(LogLevel.DEBUG, "tweet text: %s", tweetText);
                    
                    List<Token> tokens = tokenizer.tokenize(tweetText);
                    log(LogLevel.DEBUG, "tweet tokens: %d", tokens.size());
                    for (Token token : tokens)
                    {
                        String features = token.getPartOfSpeech();
                        log(LogLevel.DEBUG, "part of speech: %s", features);
                        String feature = features.split(",")[0]; 
                        if (
                                "名詞".equals(feature)
                                || "動詞".equals(feature)
                                || "形容詞".equals(feature)
                                || "形容動詞".equals(feature)
                                || "副詞".equals(feature)
                            )
                        {
                            String surfaceForm = token.getSurfaceForm();
                            log(LogLevel.DEBUG, "surface form: %s", surfaceForm);
                            Integer count = tweetWords.get(surfaceForm);
                            if (count == null)
                            {
                                count = 0;
                            }
                            log(LogLevel.DEBUG, "surface form count: %d", count);
                            count++;
                            tweetWords.put(surfaceForm, count);
                        }
                    }
                    
                    parser.skipChildren();
                }
            }
            
            parser.close();
        }
        catch (Exception e)
        {
            log(LogLevel.ERROR, "error in JSON: %s", e.getMessage());
            return new SVGCanvasView(this.emptyList, Charsets.UTF_8);
        }

        return new SVGCanvasView(tweetWords, Charsets.UTF_8);
    }
}