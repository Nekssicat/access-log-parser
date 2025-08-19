public enum HttpMethod {
    GET("GET"), PATCH("PATCH"), POST("POST"), PUT("PUT"), CONNECT("CONNECT"), HEAD("HEAD"), OPTIONS("OPTIONS"), TRACE("TRACE");
    String txt;

    HttpMethod(String txt) {
        this.txt = txt;
    }

    @Override
    public String toString() {
        return txt;
    }
}
