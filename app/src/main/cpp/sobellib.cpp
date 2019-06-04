#include <jni.h>
#include <omp.h>
#include <opencv2/opencv.hpp>
#include <cmath>
#include <cstring>
#include <string>

using namespace cv

int sobel(Mat);
int sum(int[3][3],Mat*, int i, int j);
unsigned char usqrt(unsigned char S);

extern "C" JNIEXPORT jstring JNICALL
Java_com_gmail_david_baselga_soberandroidapp_MainActivity_sobelFilter(
        JNIEnv* env,
        jobject here,
        jstring src) {

    std::string sourceFile(env->GetStringUTFChars(src, NULL));
    std::string outputFile;

    Mat image, sobelimg;

    image = imread( sourceFile , 1 );
    if( !image.data ){
        return env->NewStringUTF(sourceFile.c_str());
    }

    cvtColor(image,sobelimg,6);
    sobel(&sobelimg);

    std::string file(sourceFile);
    std::string extension;
    extension = file.substr(file.find_last_of("."));
    outputFile(file.substr(0,file.find_last_of(".")) + std::string("_output") + extension);
    imwrite( outputFile , sobelimg );

    return env->NewStringUTF(outputFile.c_str());
}

int sobel(Mat *img) {
        int S1, S2;

        int x[3][3]  = {
                                        { -1 , 0 , 1 },
                                        { -2 , 0 , 2 },
                                        { -1 , 0 , 1 }
                };
        int y[3][3] = {
                        { -1, -2, -1 },
                        { 0 , 0 , 0  },
                        { 1 , 2 , 1  }
        };

        int rows = img->rows;
        int cols = img->cols;
        Mat mag = mag.zeros(rows,cols,img->type());

        unsigned char value = 0;

        #pragma omp parallel for collapse(2) private(S1,S2,value)
        for(int i = 1 ; i < rows - 2 ; i++){
                for(int j = 1 ; j < cols - 2 ; j++){
                        S1 = sum(x, img, i, j);
                        S2 = sum(y, img, i, j);

                        value = (unsigned char) max(70.0,ceil(sqrt(S1 * S1 + S2 * S2)));
                        if(value == 70) value = 0;

                        std::memcpy(mag.data+(i*cols+j),&value,sizeof(unsigned char));
                }
        }

        std::memcpy(img->data,mag.data,cols*rows*sizeof(unsigned char));

        return 0;

}

int sum(int data[3][3], Mat *img, int y, int x){

        unsigned char dat[9] = {0,0,0,0,0,0,0,0};
        int cols = img->cols;
        std::memcpy(&dat[0],img->data+((y-1)*cols+(x-1)),sizeof(unsigned char)*3);
        std::memcpy(&dat[3],img->data+((y)*cols+(x-1)),sizeof(unsigned char)*3);
        std::memcpy(&dat[6],img->data+((y+1)*cols+(x-1)),sizeof(unsigned char)*3);
        return (int) (data[0][0] * dat[0]) + (data[0][1] * dat[1]) + (data[0][2] * dat[2]) +
                 (data[1][0] * dat[3])   + (data[1][1] * dat[4])   + (data[1][2] * dat[5]) +
                 (data[2][0] * dat[6]) + (data[2][1] * dat[7]) + (data[2][2] * dat[8]);

}