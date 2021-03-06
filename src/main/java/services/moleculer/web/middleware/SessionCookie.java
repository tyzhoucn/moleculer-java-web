/**
 * THIS SOFTWARE IS LICENSED UNDER MIT LICENSE.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
 * Based on Moleculer Framework for NodeJS [https://moleculer.services].
 * <br><br>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:<br>
 * <br>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.<br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package services.moleculer.web.middleware;

import java.net.HttpCookie;
import java.util.List;
import java.util.UUID;

import io.datatree.Tree;
import services.moleculer.Promise;
import services.moleculer.context.Context;
import services.moleculer.service.Action;
import services.moleculer.service.Middleware;
import services.moleculer.service.Name;
import services.moleculer.web.common.HttpConstants;

@Name("Session Cookie Handler")
public class SessionCookie extends Middleware implements HttpConstants {

	// --- PROPERTIES ---

	protected String cookieName = "JSESSIONID";

	protected String postfix = "; Path=/";

	// --- CONSTRUCTORS ---

	public SessionCookie() {
	}

	public SessionCookie(String cookieName) {
		this.cookieName = cookieName;
	}

	// --- CREATE NEW ACTION ---

	public Action install(Action action, Tree config) {
		return new Action() {

			@Override
			public Object handler(Context ctx) throws Exception {

				// Get cookie's value
				Tree meta = ctx.params.getMeta();
				Tree headers = meta.get(HEADERS);
				String headerValue = null;
				if (headers != null) {
					headerValue = headers.get(REQ_COOKIE, (String) null);
				}

				// Get sessionID
				String sessionID = null;
				List<HttpCookie> httpCookies = null;
				if (headerValue != null && !headerValue.isEmpty()) {
					httpCookies = HttpCookie.parse(headerValue);
					for (HttpCookie httpCookie : httpCookies) {
						if (cookieName.equals(httpCookie.getName())) {
							sessionID = httpCookie.getValue();
						}
					}
				}
				if (sessionID == null || sessionID.isEmpty()) {

					// Generate new sessionID
					sessionID = UUID.randomUUID().toString();
					final String newHeader;
					StringBuilder tmp = new StringBuilder(64);
					if (httpCookies != null) {
						for (HttpCookie httpCookie : httpCookies) {
							if (!cookieName.equals(httpCookie.getName())) {
								tmp.append(httpCookie.toString());
								tmp.append(',');
							}
						}
					}
					tmp.append(cookieName);
					tmp.append("=\"");
					tmp.append(sessionID);
					tmp.append('\"');
					if (postfix != null) {
						tmp.append(postfix);
					}
					newHeader = tmp.toString();

					// Store sessionID in meta
					meta.put("sessionID", sessionID);

					// Invoke action
					Object result = action.handler(ctx);

					// Set outgoing cookie
					return Promise.resolve(result).then(rsp -> {
						rsp.getMeta().putMap(HEADERS, true).put(RSP_SET_COOKIE, newHeader);
					});
				}

				// Store sessionID in meta
				meta.put("sessionID", sessionID);

				// Just invoke the next action
				return action.handler(ctx);
			}
		};
	}

	// --- PROPERTY GETTERS AND SETTERS ---

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String path) {
		this.postfix = path;
	}

}