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

import io.datatree.Tree;
import services.moleculer.Promise;
import services.moleculer.context.Context;
import services.moleculer.service.Action;
import services.moleculer.service.Middleware;
import services.moleculer.service.Name;
import services.moleculer.web.common.HttpConstants;

@Name("CORS Headers")
public class CorsHeaders extends Middleware implements HttpConstants {

	// --- PROPERTIES ---

	/**
	 * The Access-Control-Allow-Origin CORS header
	 */
	protected String origin = "*";

	/**
	 * The Access-Control-Allow-Methods CORS header
	 */
	protected String methods = "GET,OPTIONS,POST,PUT,DELETE";

	/**
	 * The Access-Control-Allow-Headers CORS header
	 */
	protected String allowedHeaders;

	/**
	 * The Access-Control-Expose-Headers CORS header
	 */
	protected String exposedHeaders;

	/**
	 * The Access-Control-Allow-Credentials CORS header
	 */
	protected boolean credentials;

	/**
	 * The Access-Control-Max-Age CORS header
	 */
	protected int maxAge;

	// --- ADD MIDDLEWARE TO ACTION ---

	public Action install(Action action, Tree config) {
		return new Action() {

			@Override
			public Object handler(Context ctx) throws Exception {
				return new Promise(action.handler(ctx)).then(rsp -> {
					Tree headers = rsp.getMeta().putMap(HEADERS, true);

					// Add the Access-Control-Allow-Origin header
					if (origin != null) {
						headers.put("Access-Control-Allow-Origin", origin);
					}

					// Add the Access-Control-Allow-Methods header
					if (methods != null) {
						headers.put("Access-Control-Allow-Methods", methods);
					}

					// Add the Access-Control-Allow-Headers header
					if (allowedHeaders != null) {
						headers.put("Access-Control-Allow-Headers", allowedHeaders);
					}

					// Add the Access-Control-Expose-Headers header
					if (exposedHeaders != null) {
						headers.put("Access-Control-Expose-Headers", exposedHeaders);
					}

					// Add the Access-Control-Allow-Credentials header
					headers.put("Access-Control-Allow-Credentials", Boolean.toString(credentials));

					// Add the Access-Control-Max-Age header
					if (maxAge > 0) {
						headers.put("Access-Control-Max-Age", maxAge);
					}
				});
			}

		};
	}

	// --- PROPERTY GETTERS AND SETTERS ---

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getMethods() {
		return methods;
	}

	public void setMethods(String methods) {
		this.methods = methods;
	}

	public String getAllowedHeaders() {
		return allowedHeaders;
	}

	public void setAllowedHeaders(String allowedHeaders) {
		this.allowedHeaders = allowedHeaders;
	}

	public String getExposedHeaders() {
		return exposedHeaders;
	}

	public void setExposedHeaders(String exposedHeaders) {
		this.exposedHeaders = exposedHeaders;
	}

	public boolean isCredentials() {
		return credentials;
	}

	public void setCredentials(boolean credentials) {
		this.credentials = credentials;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

}