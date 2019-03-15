package pl.pamsoft.imapcloud.restic;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public class ResticUtils {

	public static final String API_V1 = "application/vnd.x.restic.rest.v1";
	public static final String API_V2 = "application/vnd.x.restic.rest.v2";

	public static ResticVersion getAPIVersion(HttpServletRequest request) {
		return API_V2.equals(request.getHeader(HttpHeaders.ACCEPT)) ? ResticVersion.API_V2 : ResticVersion.API_V1;
	}

	public static HttpHeaders getHeaders(HttpServletRequest request) {
		return getHeaders(request, false);
	}

	public static HttpHeaders getHeaders(HttpServletRequest request, boolean isOctetStream) {
		HttpHeaders headers = new HttpHeaders();
		if (isOctetStream) {
			headers.add(HttpHeaders.CONTENT_TYPE, "binary/octet-stream");
		} else {
			headers.add(HttpHeaders.CONTENT_TYPE, API_V2.equals(request.getHeader(HttpHeaders.ACCEPT)) ? API_V2 : API_V1);
		}
		return headers;
	}

	public enum ResticVersion {API_V1, API_V2}

}
