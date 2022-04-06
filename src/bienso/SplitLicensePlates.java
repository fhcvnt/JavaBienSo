package bienso;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SplitLicensePlates {

	protected Shell shellBienso;
	private String filename = "";
	private Text textFilename;

	public static void main(String[] args) {
		System.load("C:\\Opencv\\build\\java\\x64\\opencv_java430.dll");
		try {
			SplitLicensePlates window = new SplitLicensePlates();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shellBienso.open();
		shellBienso.layout();
		while (!shellBienso.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shellBienso = new Shell();
		shellBienso.setSize(643, 606);
		shellBienso.setText("Nhận dạng biển số");
		shellBienso.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(shellBienso, SWT.NONE);

		CLabel lbImagedata = new CLabel(composite, SWT.NONE);
		lbImagedata.setAlignment(SWT.CENTER);
		lbImagedata.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lbImagedata.setBounds(10, 124, 602, 433);
		lbImagedata.setText("Image Data");

		Button btnLoadImage = new Button(composite, SWT.NONE);
		btnLoadImage.setBounds(368, 49, 85, 30);
		btnLoadImage.setText("Load Image");

		Button btnGetlicenseplate = new Button(composite, SWT.NONE);
		btnGetlicenseplate.setBounds(472, 49, 130, 30);
		btnGetlicenseplate.setText("Get license plate");

		textFilename = new Text(composite, SWT.BORDER);
		textFilename.setBounds(10, 47, 350, 30);

		// =============================================================================================================================
		// Load Image
		btnLoadImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] FILTER_NAMES = { "JPG (*.jpg)", "PNG (*.png)", "All Files (*.*)" };
				// đuôi file có thể mở
				String[] FILTER_EXTS = { "*.jpg", "*.png", "*.*" };

				FileDialog dlg = new FileDialog(shellBienso, SWT.OPEN);
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				filename = dlg.open();
				if (filename != null) {
					Path path = Paths.get(filename);
					try {
						Image image = new Image(Display.getDefault(), path.toString());
						lbImagedata.setBackground(image);
						lbImagedata.setText("");
					} catch (Exception ex) {
						System.out.println("Not the picture! - " + filename.toString());
					}
				}
				try {
					textFilename.setText(filename);
				} catch (Exception ex) {
					textFilename.setText("");
				}
			}
		});

		// ================================================================================================================
		// tim bien so
		btnGetlicenseplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Mat image = new Mat();
				image = Imgcodecs.imread(filename); // ảnh gốc
				try {
					SplitLicensePlates catbienso = new SplitLicensePlates();
					Mat bienso = new Mat();
					bienso = catbienso.getImageBiensoAuto(image);

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
					HighGui.imshow("License plates ", bienso);
					HighGui.waitKey(0);
					HighGui.destroyAllWindows();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
	}

	// ======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBiensoAuto(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 500) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(500 * im.width() / im.height(), 500));
				} else {
					Imgproc.resize(im, im, new Size(500, 500 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 7, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.adaptiveThreshold(morph_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY, 35, 5);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}
		return bienso;
	}

	// 100======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso100(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 100, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 125======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso125(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 125, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 150======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso150(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 150, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 175======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso175(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 175, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 200======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso200(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 200, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 210======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso210(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 210, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 230======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso230(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}
			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 230, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// 250======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBienso250(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 230 (resize 1.5 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 1.5, im.height() * 1.5));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 7, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.threshold(morph_image, thresh_image, 250, 255, Imgproc.THRESH_BINARY);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);

				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// ======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBiensoAuto2(Mat imagemat) {
		Mat bienso = new Mat();
		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 9, 75, 75);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.adaptiveThreshold(im_gray, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY, 35, 5);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			Mat kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// ======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBiensoAuto3(Mat imagemat) {
		Mat bienso = new Mat();

		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 7, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.adaptiveThreshold(morph_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY, 35, 5);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// ======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBiensoAuto4(Mat imagemat) {
		Mat bienso = new Mat();

		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 5, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.adaptiveThreshold(morph_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY, 35, 5);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// ======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBiensoAuto5(Mat imagemat) {
		Mat bienso = new Mat();

		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 3, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.adaptiveThreshold(morph_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY, 35, 5);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}

	// ======================================================================================================================================
	// trả về biển số là kiểu Mat, imagemat là ảnh chứa biển số
	public Mat getImageBiensoAuto6(Mat imagemat) {
		Mat bienso = new Mat();

		try {
			Mat im = new Mat();
			im = imagemat.clone(); // ảnh gốc
			// resize ảnh nếu chiều ngang ảnh nhỏ hơn 400 (resize 2 lần)
			if (im.width() < 300) {
				// Mat resizeimage = new Mat();
				Imgproc.resize(im, im, new Size(im.width() * 2, im.height() * 2));
			} else if (im.width() > 550) {
				// resize ảnh cho chiều ngang về 550 hoặc chiều cao về 550
				if (im.width() < im.height()) {
					Imgproc.resize(im, im, new Size(550 * im.width() / im.height(), 550));
				} else {
					Imgproc.resize(im, im, new Size(550, 550 * im.height() / im.width()));
				}
			}

			Mat image = im.clone();
			Mat im_gray = new Mat();
			Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
			// Remove noise giảm noise và tăng edge(làm egde thêm sắc nhọn edges sharp)
			Mat noise_removal = new Mat();
			Imgproc.bilateralFilter(im_gray, noise_removal, 5, 75, 75);

			// Cân bằng lại histogram làm cho ảnh không quá sáng hoặc tối
			Mat equal_histogram = new Mat();
			Imgproc.equalizeHist(noise_removal, equal_histogram);

			// Morphogoly open mục đích là giảm egde nhiễu , egde thật thêm sắc nhọn bằng
			// cv2.morphologyEx sử dụng kerel 5x5
			Mat morph_image = new Mat();
			Mat kernel = new Mat();
			Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
			Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

			// anh nhi phan (trang den)
			Mat thresh_image = new Mat();
			Imgproc.adaptiveThreshold(morph_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
					Imgproc.THRESH_BINARY, 35, 5);
			// tim bien anh
			Mat canny_image = new Mat();
			Imgproc.Canny(thresh_image, canny_image, 250, 255);
			kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);

			// dilate để tăng sharp cho egde
			Mat dilated_image = new Mat();
			Imgproc.dilate(canny_image, dilated_image, kernel);

			List<MatOfPoint> contours = new ArrayList<>();
			contours.clear();
			Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

			// vẽ hình chữ nhật quanh biển số
			// Biển số xe máy Chiều cao 140 mm, chiều dài 190 mm
			/*
			 * kiểm tra xem đâu là biển số trong danh sách các đường biên có được nếu là
			 * biển số thì phải chứa ít nhất 6 khung hình chữ nhật chứa các ký tự
			 */
			int dem = 0;// Đếm số ký tự trong biển số
			int vitribienso = -1; // vị trí biển số trong contours
			// Danh sách ký tự
			List<Integer> listchar = new ArrayList<>();
			listchar.clear();
			for (int i = 0; i < contours.size(); i++) {
				Rect r = Imgproc.boundingRect(contours.get(i));
				if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 2.2) {
					dem = 0;// Đếm số ký tự trong biển số
					// Danh sách vị trí ký tự trong contours
					listchar.clear(); // làm rỗng danh sách
					boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
					for (int check = 0; check < contours.size(); check++) {
						if (check != i) {
							Rect rcheck = Imgproc.boundingRect(contours.get(check));
							if (!(rcheck.width / (double) rcheck.height > 1.0
									&& rcheck.width / (double) rcheck.height < 2.2)) {
								// Tìm ký tự nằm trong biển số
								if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
										&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
										&& (rcheck.height > (float) r.height / 3.5
												&& rcheck.height < (float) r.height / 2)) {
									duplicate = false;
									// loại bỏ những ký tự bị nhân đôi
									for (int k = 0; k < listchar.size(); k++) {
										try {
											Rect rcheckduplicate = Imgproc.boundingRect(contours.get(listchar.get(k)));
											if (listchar.get(k) != check) {
												if (((rcheck.x + rcheck.width / 2 > rcheckduplicate.x)
														&& (rcheck.x + rcheck.width / 2) < (rcheckduplicate.x
																+ rcheckduplicate.width)
														&& (rcheck.y + rcheck.height / 2) > rcheckduplicate.y
														&& (rcheck.y + rcheck.height / 2) < (rcheckduplicate.y
																+ rcheckduplicate.height))) {
													duplicate = true;
												}
											}
										} catch (Exception exc) {
										}
									}
									if (!duplicate) {
										dem++;
										listchar.add(check);
									}
								}
							}
						}
					}
				}
				if (dem > 3) {
					vitribienso = i;
				}
				// Kiểm tra nếu tìm được biển số thì thoát khỏi vòng lặp
				if (vitribienso >= 0) {
					break;
				}
			}

			try {
				// Cắt lấy biển số
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					bienso = new Mat(image, rbienso);
				} else {
					bienso = null;
				}
			} catch (Exception ae) {
				ae.printStackTrace();
			}
		} catch (Exception except) {
			except.printStackTrace();
		}

		return bienso;
	}
}
