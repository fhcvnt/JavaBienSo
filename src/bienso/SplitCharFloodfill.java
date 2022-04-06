package bienso;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SplitCharFloodfill {
	private int countchartop = 0;
	private int countcharbottom = 0;

	public SplitCharFloodfill() {

	}

	// ======================================================================================================
	// Hàm lấy danh sách ký tự trong biển số
	public List<Mat> getListChar(Mat bienso) {
		// Danh sách ảnh ký tự đã được cắt ra và nhị phân luôn
		List<Mat> danhsachkytu = new ArrayList<>();
		int demsokytu = 0;
		try {
			Mat image = bienso.clone();
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
			Mat src = new Mat();
			Mat threshold_image = new Mat();

			// tính tỷ lệ chiều ngang, chiều cao ảnh, nếu chiều ngang/chiều cao >1.5 thì ta
			// sẽ reszie ảnh về tỷ lệ 1.3
			if (image.width() / (float) image.height() > 1.5) {
				Imgproc.resize(image, image, new Size(image.height() * 1.3, image.height()));

				// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
				Mat noise_removal = new Mat();
				Imgproc.bilateralFilter(image, noise_removal, 8, 75, 75);

				image = noise_removal.clone();
				// resize image cho chiều ngang ảnh bằng 300
				Imgproc.resize(image, image, new Size(300, image.height() * 300 / image.width()));
				src = image.clone();

				threshold_image = new Mat();

				Imgproc.adaptiveThreshold(image, threshold_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
						Imgproc.THRESH_BINARY_INV, 35, 5);

				danhsachkytu.clear();
				Mat image_nhiphan = threshold_image.clone();

				demsokytu = (int) threshold_image.size().height / 4;
				countchartop = 0;
				countcharbottom = 0;
				for (int y = (int) threshold_image.size().height / 4; y < threshold_image.size().height; y = y
						+ (int) threshold_image.size().height / 8) {
					for (int x = 0; x < threshold_image.size().width; x++) {
						double[] data = threshold_image.get(y, x);
						try {
							if (data[0] != 255) {
								continue;
							}
						} catch (NullPointerException ne) {
							ne.printStackTrace();
						}

						Rect rectxy = new Rect();
						// Tách ký tự bằng floodfill
						Imgproc.floodFill(threshold_image, new Mat(), new Point(x, y), new Scalar(180), rectxy,
								new Scalar(0));
						if (rectxy.width < src.width() / 4 && rectxy.width > src.width() / 17
								&& (float) rectxy.height < src.height() / 1.8 && rectxy.height > src.height() / 5) {
							// tính số lượng ký tự dòng trên, dòng dưới
							if (demsokytu == y) {
								countchartop++;
							} else {
								countcharbottom++;
							}

							// Cắt ký tự khỏi biển số và loại bỏ nền thừa chỉ còn ký tự màu đen nền trắng
							Mat anhnhiphan = image_nhiphan.clone();
							Rect rectij = new Rect();

							Imgproc.floodFill(anhnhiphan, new Mat(), new Point(x, y), new Scalar(180), rectij,
									new Scalar(0));

							Mat matfill = new Mat();
							matfill = new Mat(anhnhiphan, rectij);

							Mat kytuchuan = matfill.clone();

							for (int j = 0; j < matfill.size().height; j++) {
								for (int i = 0; i < matfill.size().width; i++) {
									double[] dataij = matfill.get(j, i);
									try {
										if (dataij[0] == 255) {
											kytuchuan.put(j, i, dataij);
										} else if (dataij[0] == 0) {
											dataij[0] = 255;
											kytuchuan.put(j, i, dataij);
										} else {
											dataij[0] = 0;
											kytuchuan.put(j, i, dataij);
										}
									} catch (NullPointerException ne) {
										ne.printStackTrace();
									}
								}
							}
							danhsachkytu.add(kytuchuan);
						}
					}
				}
			} else {
				// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
				Mat noise_removal = new Mat();
				Imgproc.bilateralFilter(image, noise_removal, 8, 75, 75);

				image = noise_removal.clone();
				// resize image cho chiều ngang ảnh bằng 300
				Imgproc.resize(image, image, new Size(300, image.height() * 300 / image.width()));
				src = image.clone();

				threshold_image = new Mat();

				Imgproc.adaptiveThreshold(image, threshold_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
						Imgproc.THRESH_BINARY_INV, 35, 5);

				danhsachkytu.clear();
				Mat image_nhiphan = threshold_image.clone();

				demsokytu = (int) threshold_image.size().height / 4;
				countchartop = 0;
				countcharbottom = 0;
				for (int y = (int) threshold_image.size().height / 4; y < threshold_image.size().height; y = y
						+ (int) threshold_image.size().height / 8) {
					for (int x = 0; x < threshold_image.size().width; x++) {
						double[] data = threshold_image.get(y, x);
						try {
							if (data[0] != 255) {
								continue;
							}
						} catch (NullPointerException ne) {
							ne.printStackTrace();
						}

						Rect rectxy = new Rect();
						// Tách ký tự bằng floodfill
						Imgproc.floodFill(threshold_image, new Mat(), new Point(x, y), new Scalar(180), rectxy,
								new Scalar(0));
						if (rectxy.width < src.width() / 4 && rectxy.width > src.width() / 17
								&& rectxy.height < src.height() / 2 && rectxy.height > src.height() / 5) {
							// tính số lượng ký tự dòng trên, dòng dưới
							if (demsokytu == y) {
								countchartop++;
							} else {
								countcharbottom++;
							}

							// Cắt ký tự khỏi biển số và loại bỏ nền thừa chỉ còn ký tự màu đen nền trắng
							Mat anhnhiphan = image_nhiphan.clone();
							Rect rectij = new Rect();

							Imgproc.floodFill(anhnhiphan, new Mat(), new Point(x, y), new Scalar(180), rectij,
									new Scalar(0));

							Mat matfill = new Mat();
							matfill = new Mat(anhnhiphan, rectij);

							Mat kytuchuan = matfill.clone();

							for (int j = 0; j < matfill.size().height; j++) {
								for (int i = 0; i < matfill.size().width; i++) {
									double[] dataij = matfill.get(j, i);
									try {
										if (dataij[0] == 255) {
											kytuchuan.put(j, i, dataij);
										} else if (dataij[0] == 0) {
											dataij[0] = 255;
											kytuchuan.put(j, i, dataij);
										} else {
											dataij[0] = 0;
											kytuchuan.put(j, i, dataij);
										}
									} catch (NullPointerException ne) {
										ne.printStackTrace();
									}
								}
							}
							danhsachkytu.add(kytuchuan);
						}
					}
				}
			}
			Imgcodecs.imwrite("D:/Bienso/Save/floodfill.jpg", threshold_image);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return danhsachkytu;
	}

	// =============================================================================================================
	// lấy số lượng ký tự dòng trên
	public int getCountchartop() {
		return countchartop;
	}

	// =============================================================================================================
	// lấy số lượng ký tự dòng dưới
	public int getCountcharbottom() {
		return countcharbottom;
	}
}
