package bienso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TestCatBienso {
	public static void main(String[] args) {
		System.load("C:/Opencv/build/java/x64/opencv_java430.dll");
		String database = "D:/Bienso/SplitLicensePlates";
		File[] files = new File(database).listFiles();
		for (int i = 0; i < files.length; i++) {
			Mat image = Imgcodecs.imread(files[i].getAbsolutePath());
			Mat img=image.clone();
			Imgcodecs.imwrite("D:/Bienso/Save/" + files[i].getName() , img);
			List<Mat> listcharimage = new ArrayList<Mat>();
			try {
				SplitLicensePlates catbienso = new SplitLicensePlates();
				Mat bienso = catbienso.getImageBiensoAuto(image);
				if (bienso == null) {
					bienso = catbienso.getImageBiensoAuto2(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBiensoAuto3(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBiensoAuto4(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBiensoAuto5(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBiensoAuto6(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso100(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso125(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso150(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso175(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso200(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso210(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso230(image);
				}
				if (bienso == null) {
					bienso = catbienso.getImageBienso250(image);
				}


				// Tách ký tự
				SplitCharFloodfill kytu = new SplitCharFloodfill();
				listcharimage.clear();
				listcharimage = kytu.getListChar(bienso);

				int sokytu = 0;
				for (Mat imagechar : listcharimage) {
					try {
						// dữ liệu test phải là ảnh 22*40 -----------------------------------------
						Mat imagetest = new Mat();
						Imgproc.resize(imagechar, imagetest, new Size(22, 40));
						// lưu ảnh ******************
						Imgcodecs.imwrite("D:/Bienso/Save/"+files[i].getName() + sokytu + ".jpg", imagetest);

					} catch (Exception exc) {
						exc.printStackTrace();
					}
					sokytu++;
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}

		}
	}
}
