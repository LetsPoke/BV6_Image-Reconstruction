package uebung6;

public class Prediction {

    private static float MSE = 0;

    public enum FilterType {
        A ("A (Horizontal)"),
        B ("B (Vertical)"),
        C ("C (Diagonal)"),
        ABC ("A+B-C"),
        ADAPTIV ("adaptiv");

        private final String name;
        FilterType(String s) { name = s; }
        public String toString() { return this.name; }
    };

//    public void copy(RasterImage src, RasterImage dst) {
//        for(int y=0; y<src.height; y++){
//            for(int x=0; x<src.width; x++){
//                int pos = y * src.width + x;
//                dst.argb[pos] = src.argb[pos];
//            }
//        }
//    }

    public static float getMSE(){
        return MSE;
    }

    public static void caseA(RasterImage src, RasterImage dst, RasterImage reconstructedImage, float quantizationValue){
        for(int y=0; y<src.height; y++){
            for(int x=0; x<src.width; x++){
                //  C | B
                //  A | X
                int pos = y * src.width + x;
                int posA = pos - 1;

                int A = 128;
                if(posA >= 0 && pos < src.argb.length){
                    A = src.argb[posA] & 0xff;
                }

                int X = src.argb[pos] & 0xff;
                int e = X - A ;


                predictionImage(pos, dst, e);
                reconstructedImage(pos, reconstructedImage, posA, e);

                MSE = MSE + e * e; //Square and add
            }
        }
        MSE = MSE/src.argb.length; //mean
    }

    public static void caseB(RasterImage src, RasterImage dst, RasterImage reconstructedImage, float quantizationValue){
        for(int y=0; y<src.height; y++){
            for(int x=0; x<src.width; x++){
                //  C | B
                //  A | X
                int pos = y * src.width + x;
                int posB = pos - src.width;

                int B = 128;
                if(posB >= 0 && pos < src.argb.length){
                    B = src.argb[posB] & 0xff;
                }

                int X = src.argb[pos] & 0xff;
                int e = X - B ;
                //Quantization
                int q = Math.round(e/quantizationValue);
                e = Math.round(q * quantizationValue);

                predictionImage(pos, dst, e);
                reconstructedImage(pos, reconstructedImage, posB, e);

                MSE = MSE + e * e;
            }
        }
        MSE = MSE/src.argb.length;
    }

    public static void caseC(RasterImage src, RasterImage dst, RasterImage reconstructedImage, float quantizationValue){
        for(int y=0; y<src.height; y++){
            for(int x=0; x<src.width; x++){
                //  C | B
                //  A | X
                int pos = y * src.width + x;
                int posC = pos - src.width - 1;

                int C = 128;
                if(posC >= 0 && pos < src.argb.length){
                    C = src.argb[posC] & 0xff;
                }

                int X = src.argb[pos] & 0xff;
                int e = X - C ;
                //Quantization
                int q = Math.round(e/quantizationValue);
                e = Math.round(q * quantizationValue);

                predictionImage(pos, dst, e);
                reconstructedImage(pos, reconstructedImage, posC, e);

                MSE = MSE + e * e;
            }
        }
        MSE = MSE/src.argb.length;
    }

    public static void caseABC(RasterImage src, RasterImage dst, RasterImage reconstructedImage, float quantizationValue){
        for(int y=0; y<src.height; y++){
            for(int x=0; x<src.width; x++){
                //  C | B
                //  A | X
                int pos = y * src.width + x;
                int posA = pos - 1;
                int posB = pos - src.width;
                int posC = pos - src.width - 1;
                int posABC = posA + posB - posC;
                //System.out.println(posA + " " + posB + " " + posC + " " + posABC);

                int A = 128;
                if(posA >= 0 && pos < src.argb.length){
                    A = src.argb[posA] & 0xff;
                }
                int B = 128;
                if(posB >= 0 && pos < src.argb.length){
                    B = src.argb[posB] & 0xff;
                }
                int C = 128;
                if(posC >= 0 && pos < src.argb.length){
                    C = src.argb[posC] & 0xff;
                }

                int X = src.argb[pos] & 0xff;
                int e = X - (A + B - C);
                //Quantization
                int q = Math.round(e/quantizationValue);
                e = Math.round(q * quantizationValue);

                predictionImage(pos, dst, e);
                //reconstructedImage(pos, reconstructedImage, posABC, e);
                int oof = (A + B - C) + e;
                reconstructedImage.argb[pos] = 0xff << 24 | oof << 16 | oof << 8 | oof;



                MSE = MSE + e * e;
            }
        }
        MSE = MSE/src.argb.length;
    }

    public static void caseAdaptiv(RasterImage src, RasterImage dst, RasterImage reconstructedImage, float quantizationValue){
        for(int y=0; y<src.height; y++){
            for(int x=0; x<src.width; x++){
                //  C | B
                //  A | X
                int pos = y * src.width + x;
                int posA = pos - 1;
                int posB = pos - src.width;
                int posC = pos - src.width - 1;

                int A = 128;
                if(posA >= 0 && pos < src.argb.length){
                    A = src.argb[posA] & 0xff;
                }
                int B = 128;
                if(posB >= 0 && pos < src.argb.length){
                    B = src.argb[posB] & 0xff;
                }
                int C = 128;
                if(posC >= 0 && pos < src.argb.length){
                    C = src.argb[posC] & 0xff;
                }

                int X = src.argb[pos] & 0xff;
                int e;

                if(Math.abs(A - C) < Math.abs(B - C)){
                    e = X - B;
                    //Quantization
                    int q = Math.round(e/quantizationValue);
                    e = Math.round(q * quantizationValue);

                    predictionImage(pos, dst, e);
                    reconstructedImage(pos, reconstructedImage, posB, e);
                } else {
                    e = X - A;
                    //Quantization
                    int q = Math.round(e/quantizationValue);
                    e = Math.round(q * quantizationValue);

                    predictionImage(pos, dst, e);
                    reconstructedImage(pos, reconstructedImage, posA, e);
                }
                MSE = MSE + e * e;
            }
        }
        MSE = MSE/src.argb.length;
    }

    public static void predictionImage(int pos, RasterImage dst, int e){
        int color = e + 128;
        if(color >= 255){ color = 255; }
        if(color <= 0){ color = 0; }

        dst.argb[pos] = 0xff << 24 | color << 16 | color << 8 | color;
    }

    public static void reconstructedImage(int pos, RasterImage reconstructedImage, int posA, int e){
        int pix = 128;
        if(posA >= 0 && posA < reconstructedImage.argb.length){
            pix = reconstructedImage.argb[posA];
        }
        int pix2 = (pix + e) & 0xff;

        reconstructedImage.argb[pos] = 0xff << 24 | pix2 << 16 | pix2 << 8 | pix2;
    }

    public static double entropy(RasterImage image){
        double entropy = 0;
        int[] histogram = new int[256];
        for(int index = 0; index < image.argb.length; index++) {
            histogram[image.argb[index] & 0xff]++;
        }

        for(int index = 0; index < histogram.length; index++) {
            if (histogram[index] != 0) {
                double pj = (double)image.argb.length/histogram[index];
                entropy = entropy + -1 * ((1 / pj) * (((-1) * Math.log(pj)) / Math.log(2)));
            }
        }
        return entropy;
    }
}
