part of mapViewer;

putPixel(Uint8ClampedList data, int width, int x, int y, int r, int g, int b, int a) {
    int i = (x + y * width) * 4;

    if (a == 0) return;

    if (data[i + 3] == 0) {
        data[i] = r;
        data[i + 1] = g;
        data[i + 2] = b;
        data[i + 3] = 255;
    } else {
        data[i] = (data[i] * ((255-a)/255) + r * (a/255)).toInt();
        data[i + 1] = (data[i + 1] * ((255-a)/255) + g * (a/255)).toInt();
        data[i + 2] = (data[i + 2] * ((255-a)/255) + b * (a/255)).toInt();
        data[i + 3] = 255;
    }
}
