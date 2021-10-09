#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES sTexture;
uniform float S;
uniform float H;
uniform float L;

vec3 rgb2hsl(vec3 rgb) {
  float r = rgb.x;
  float g = rgb.y;
  float b = rgb.z;
  float max = max(r, max(g, b));
  float min = min(r, min(g, b));
  float h;
  float s;
  float l = (max + min) / 2.0;
  if (max == min) {
    h = 0.0;
    s = 0.0;
  } else {
    float d = max - min;
    s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);
    if (max == r) {
      h = ((g - b) / d + (g < b ? 6.0 : 0.0)) / 6.0;
    } else if (max == g) {
      h = ((b - r) / d + 2.0) / 6.0;
    } else {
      h = ((r - g) / d + 4.0) / 6.0;
    }
  }
  return vec3(h, s, l);
}

float hue2rgb(float f1, float f2, float hue) {
    if (hue < 0.0)
        hue += 1.0;
    else if (hue > 1.0)
        hue -= 1.0;
    float res;
    if ((6.0 * hue) < 1.0)
        res = f1 + (f2 - f1) * 6.0 * hue;
    else if ((2.0 * hue) < 1.0)
        res = f2;
    else if ((3.0 * hue) < 2.0)
        res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;
    else
        res = f1;
    return res;
}



vec3 hsl2rgb(vec3 hsl) {
    vec3 rgb;
    if (hsl.y == 0.0) {
        rgb = vec3(hsl.z);
    } else {
        float f2;
        if (hsl.z < 0.5)
            f2 = hsl.z * (1.0 + hsl.y);
        else
            f2 = hsl.z + hsl.y - hsl.y * hsl.z;
        float f1 = 2.0 * hsl.z - f2;
        rgb.r = hue2rgb(f1, f2, hsl.x + (1.0/3.0));
        rgb.g = hue2rgb(f1, f2, hsl.x);
        rgb.b = hue2rgb(f1, f2, hsl.x - (1.0/3.0));
    }
    return rgb;
}

vec3 rgb2hsv(vec3 c){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec4 tc = texture2D(sTexture, textureCoordinate);
    vec3 hsl = rgb2hsv(tc.xyz);
    if(S != 1.0)hsl.y = hsl.y*S;
    if(H != 0.0)hsl.x = H;
    if(hsl.x<0.0)hsl.x = hsl.x+1.0;
    else if(hsl.x>1.0)hsl.x = hsl.x-1.0;
    if(L != 1.0)hsl.z = hsl.z*L;
    vec3 rgb = hsv2rgb(hsl);
    gl_FragColor = vec4(rgb,tc.w);
}