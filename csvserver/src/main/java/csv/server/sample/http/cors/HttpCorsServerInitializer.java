package csv.server.sample.http.cors;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Please refer to the {@link CorsConfig} javadocs for information about all the
 * configuration options available.
 *
 * Below are some of configuration discussed in this example:
 * <h3>Support only a specific origin</h3>
 * To support a single origin instead of the wildcard use the following:
 * <pre>
 * CorsConfig corsConfig = CorsConfig.withOrigin("http://domain1.com")
 * </pre>
 *
 * <h3>Enable loading from the file system</h3>
 * To enable the server to handle an origin specified as 'null', which happens
 * when a web browser loads a file from the local file system use the following:
 * <pre>
 * corsConfig.isNullOriginAllowed()
 * </pre>
 *
 * <h3>Enable request headers</h3>
 * To enable additional request headers:
 * <pre>
 * corsConfig.allowedRequestHeaders("custom-request-header")
 * </pre>
 *
 * <h3>Expose response headers</h3>
 * By default a browser only exposes the following simple header:
 * <ul>
 * <li>Cache-Control</li>
 * <li>Content-Language</li>
 * <li>Content-Type</li>
 * <li>Expires</li>
 * <li>Last-Modified</li>
 * <li>Pragma</li>
 * </ul>
 * Any of the above response headers can be retrieved by:
 * <pre>
 * xhr.getResponseHeader("Content-Type");
 * </pre>
 * If you need to get access to other headers this must be enabled by the server, for example:
 * <pre>
 * corsConfig.exposedHeaders("custom-response-header");
 * </pre>
 *
 * @author Neal Shan
 * @since 0.0.1
 */
public class HttpCorsServerInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel ch) throws Exception {
        CorsConfig corsConfig = CorsConfigBuilder
                //.forAnyOrigin()
                .forOrigin("http://asd.com:8088") //TODO: ??
                .allowedRequestHeaders("custom-request-header")
                .exposeHeaders("custom-response-header")
                .allowNullOrigin()
                .allowCredentials()
                .build();

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpResponseEncoder())
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new ChunkedWriteHandler())
                .addLast(new CorsHandler(corsConfig))
                .addLast(new OkResponseHandler());
    }
}
