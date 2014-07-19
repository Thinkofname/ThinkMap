/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.thinkcraft.bukkit.web;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import uk.co.thinkofdeath.thinkcraft.bukkit.ThinkMapPlugin;

import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class InternalWebServer extends EndPoint {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    private final static HashMap<String, String> mimeTypes = new HashMap<String, String>();

    static {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("css", "text/css");
    }


    private final SimpleDateFormat format = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
    private final ThinkMapPlugin plugin;

    public InternalWebServer(ThinkMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(ChannelHandlerContext context, URI uri, FullHttpRequest request) throws Exception {
        if (request.getMethod() != HttpMethod.GET) {
            sendHttpResponse(context, request, new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED));
            return;
        }
        String modified = request.headers().get(IF_MODIFIED_SINCE);
        if (modified != null && !modified.isEmpty()) {
            Date modifiedDate = format.parse(modified);

            if (modifiedDate.equals(plugin.getStartUpDate())) {
                sendHttpResponse(context, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED));
                return;
            }
        }

        String path = uri.getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }

        ByteBuf buffer;
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("www" + path)) {
            if (stream == null) {
                sendHttpResponse(context, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                return;
            }
            ByteBufOutputStream out = new ByteBufOutputStream(context.alloc().buffer());
            IOUtils.copy(stream, out);
            buffer = out.buffer();
            out.close();
        }

        if (path.equals("/index.html")) {
            String page = buffer.toString(Charsets.UTF_8);
            page = page.replaceAll("%SERVERPORT%", Integer.toString(plugin.getConfiguration().getPort()));
            buffer.release();
            buffer = Unpooled.wrappedBuffer(page.getBytes(Charsets.UTF_8));
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);

        response.headers().set(DATE, format.format(new Date()));
        response.headers().set(LAST_MODIFIED, format.format(plugin.getStartUpDate()));


        String ext = path.substring(path.lastIndexOf('.') + 1);
        String type = mimeTypes.containsKey(ext) ? mimeTypes.get(ext) : "text/plain";
        if (type.startsWith("text/")) {
            type += "; charset=UTF-8";
        }
        response.headers().set(CONTENT_TYPE, type);
        sendHttpResponse(context, request, response);
    }
}
