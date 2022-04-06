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
import org.eclipse.wb.swt.SWTResourceManager;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TestCatBienSoError {

	protected Shell shell;
	private String filename = "";

	public static void main(String[] args) {
		System.load("C:\\Opencv\\build\\java\\x64\\opencv_java430.dll");
		try {
			TestCatBienSoError window = new TestCatBienSoError();
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
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(680, 411);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(shell, SWT.NONE);

		CLabel lbImagedata = new CLabel(composite, SWT.NONE);
		lbImagedata.setAlignment(SWT.CENTER);
		lbImagedata.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lbImagedata.setBounds(33, 23, 307, 225);
		lbImagedata.setText("Image Data");

		Button btnLoadImage = new Button(composite, SWT.NONE);
		btnLoadImage.setBounds(368, 49, 85, 40);
		btnLoadImage.setText("Load Image");

		Button getBienSo = new Button(composite, SWT.NONE);
		getBienSo.setBounds(33, 266, 117, 52);
		getBienSo.setText("Get Bien So");

		Button btnSplitLicensePlates = new Button(composite, SWT.NONE);
		btnSplitLicensePlates.setBounds(169, 266, 142, 52);
		btnSplitLicensePlates.setText("Split License Plates");

		// =============================================================================================================================
		// Load Image
		btnLoadImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] FILTER_NAMES = { "JPG (*.jpg)", "PNG (*.png)", "All Files (*.*)" };
				// đuôi file có thể mở
				String[] FILTER_EXTS = { "*.jpg", "*.png", "*.*" };

				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
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
			}
		});

		// =============================================================================================================================
		// Tim bien so
		getBienSo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Mat im = Imgcodecs.imread(filename); // ảnh gốc
				Mat im_gray = new Mat();
				Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
				Mat noise_removal = new Mat();
				Imgproc.bilateralFilter(im_gray, noise_removal, 7, 75, 75);

				// Làm mờ ảnh
				// Imgproc.GaussianBlur(im_gray, im_gray, new Size(9, 9), 5);

				Mat equal_histogram = new Mat();
				Imgproc.equalizeHist(noise_removal, equal_histogram);
				Mat kernel = new Mat();
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));

				Mat morph_image = new Mat();
				Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

				// xóa phông (background) không cần thiết
				Mat sub_morp_image = new Mat();
				// Core.subtract(equal_histogram, morph_image, sub_morp_image);
				sub_morp_image = morph_image.clone();

				Mat thresh_image = new Mat(); // anh nhi phan (trang den)
				Imgproc.adaptiveThreshold(sub_morp_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
						Imgproc.THRESH_BINARY, 35, 5);
				Mat canny_image = new Mat(); // tim bien anh
				Imgproc.Canny(thresh_image, canny_image, 250, 255);
				kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);
				// kernel = Mat.ones(new Size(3, 3), CvType.CV_8UC1);
				Mat dilated_image = new Mat();
				Imgproc.dilate(canny_image, dilated_image, kernel);

				// ******************************************************************
				List<MatOfPoint> contours = new ArrayList<>();
				Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
				// Imgproc.findContours(dilated_image, contours, new Mat(),
				// Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

				// vẽ hình chữ nhật quanh biển số
				// Biển số xe mô tô Chi�?u cao 140 mm, chi�?u dài 190 mm
				/*
				 * kiểm tra xem đâu là biển số trong danh sách các đư�?ng biên có được nếu là
				 * biển số thì phải chứa ít nhất 7 khung hình chữ nhật chứa các ký tự
				 */
				int dem = 0;// �?ếm số ký tự trong biển số
				int vitribienso = -1; // vị trí biển số trong contours
				List<Integer> listchar = new ArrayList<>();
				for (int i = 0; i < contours.size(); i++) {
					Rect r = Imgproc.boundingRect(contours.get(i));
					Imgproc.rectangle(im, r, new Scalar(0, 0, 255), 1, 8, 0);
					if (r.width / (double) r.height > 1.0 && r.width / (double) r.height < 1.5) {
						dem = 0;// �?ếm số ký tự trong biển số
						// Danh sách vị trí ký tự trong contours
						listchar.clear(); // làm rỗng danh sách
						boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
						for (int check = 0; check < contours.size(); check++) {
							if (check != i) {
								Rect rcheck = Imgproc.boundingRect(contours.get(check));
								if (!(rcheck.width / (double) rcheck.height > 1.0
										&& rcheck.width / (double) rcheck.height < 1.5)) {
									// Tìm ký tự nằm trong biển số
									if ((rcheck.x > r.x && rcheck.x < r.x + r.width) && rcheck.width < rcheck.height
											&& (rcheck.y > r.y && rcheck.y < r.y + r.height)
											&& (rcheck.height > (float) r.height / 3
													&& rcheck.height < (float) r.height / 2)) {
										duplicate = false;
										// loại b�? những ký tự bị nhân đôi
										for (int k = 0; k < listchar.size(); k++) {
											try {
												Rect rcheckduplicate = Imgproc
														.boundingRect(contours.get(listchar.get(k)));
												if (listchar.get(k) != check) {
													if (((rcheck.x + rcheck.width / 2) > rcheckduplicate.x
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
											Imgproc.rectangle(im, rcheck, new Scalar(255, 0, 0), 1, 8, 0);
										}
									}
								}
							}
						}
					}
					if (dem > 3) {
						vitribienso = i;
					}
					// Kiểm tra nếu tìm được biển số thì thoát kh�?i vòng lặp
					if (vitribienso >= 0) {
						break;
					}
				}

				// Cắt lấy biển số
				Mat catbienso = null;
				if (vitribienso >= 0) {
					Rect rbienso = Imgproc.boundingRect(contours.get(vitribienso));
					// Imgproc.rectangle(im, rbienso, new Scalar(0, 0, 255), 2, 8, 0); // cat bien
					// so Rect
					// Rect rectCrop = new Rect(rbienso.x, rbienso.y, rbienso.width,
					// rbienso.height);
					catbienso = new Mat(thresh_image, rbienso);
				}

				// Cắt lấy các ký tự trong biển số
				List<Mat> listcharimage = new ArrayList<Mat>();
				for (int vitri = 0; vitri < listchar.size(); vitri++) {
					Rect rkytu = Imgproc.boundingRect(contours.get(listchar.get(vitri)));
					listcharimage.add(new Mat(thresh_image, rkytu));
				}

				// trắng/đen=0.7 đến 1.4

				HighGui.imshow("Anh goc", im);
				// HighGui.imshow("Bien so", catbienso);
				int a = 0;
				for (Mat imagechar : listcharimage) {
					HighGui.imshow("Ky tu - " + a, imagechar);
					a++;
				}
				HighGui.waitKey(0);

			}
		});

		// -----------------------------------------------------------------------------------------
		btnSplitLicensePlates.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Mat im = Imgcodecs.imread(filename); // ảnh gốc
				Mat image = im.clone();
				Mat im_gray = new Mat();
				Imgproc.cvtColor(im, im_gray, Imgproc.COLOR_BGR2GRAY);
				Mat noise_removal = new Mat();
				Imgproc.bilateralFilter(im_gray, noise_removal, 7, 75, 75);

				// Làm mờ ảnh
				// Imgproc.GaussianBlur(im_gray, im_gray, new Size(9, 9), 5);

				Mat equal_histogram = new Mat();
				Imgproc.equalizeHist(noise_removal, equal_histogram);
				Mat kernel = new Mat();
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));

				Mat morph_image = new Mat();
				Imgproc.morphologyEx(equal_histogram, morph_image, Imgproc.MORPH_OPEN, kernel);

				// xóa phông (background) không cần thiết
				Mat sub_morp_image = new Mat();
				// Core.subtract(equal_histogram, morph_image, sub_morp_image);
				sub_morp_image = morph_image.clone();

				Mat thresh_image = new Mat(); // anh nhi phan (trang den)
				Imgproc.adaptiveThreshold(sub_morp_image, thresh_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
						Imgproc.THRESH_BINARY, 35, 5);
				Mat canny_image = new Mat(); // tim bien anh
				Imgproc.Canny(thresh_image, canny_image, 250, 255);
				kernel = Mat.ones(new Size(2, 2), CvType.CV_32F);
				Mat dilated_image = new Mat();
				Imgproc.dilate(canny_image, dilated_image, kernel);

				// ******************************************************************
				List<MatOfPoint> contours = new ArrayList<>();
				Imgproc.findContours(dilated_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

				// vẽ hình chữ nhật quanh biển số
				// Biển số xe mô tô Chi�?u cao 140 mm, chi�?u dài 190 mm
				/*
				 * kiểm tra xem đâu là biển số trong danh sách các đư�?ng biên có được nếu là
				 * biển số thì phải chứa ít nhất 7 khung hình chữ nhật chứa các ký tự
				 */
				boolean duplicate = false; // kiểm tra ký tự bị nhân đôi
				int dem = 0;// Đếm số ký tự trong biển số
				List<Rect> listrectchar = new ArrayList<>();
				listrectchar.clear();
				for (int i = 0; i < contours.size(); i++) {
					Rect r = Imgproc.boundingRect(contours.get(i));

					if (r.height / (double) r.width > 1.35) {
						listrectchar.add(r);
					}
				}
				dem = 0;
				List<Rect> listrectchar30 = new ArrayList<>();
				for (int i = 0; i < listrectchar.size(); i++) {
					for (int j = 0; j < listrectchar.size(); j++) {
						if (listrectchar.get(i).height > listrectchar.get(j).height) {
							dem++;
						}
					}
					if (dem >= listrectchar.size() - 30) {
						duplicate = false; // kiểm tra ký tự bị nhân đôi
						for (Rect check : listrectchar30) {
							// loại bỏ những ký tự bị nhân đôi
							if ((listrectchar.get(i).x + listrectchar.get(i).width / 2 > check.x
									&& listrectchar.get(i).x + listrectchar.get(i).width / 2 < check.x + check.width
									&& listrectchar.get(i).y + listrectchar.get(i).height / 2 > check.y
									&& listrectchar.get(i).y + listrectchar.get(i).height / 2 < check.y
											+ check.height)) {
								duplicate = true;
								break;
							}
						}
						if (!duplicate) {
							listrectchar30.add(listrectchar.get(i));
						}

					}
					dem = 0;
				}

				int demkytu = 1;
				List<Integer> listvitri = new ArrayList<Integer>();

				for (int i = 0; i < listrectchar30.size(); i++) {
					// Imgproc.rectangle(im, listrectchar30.get(i), new Scalar(0, 0, 255), 1, 8, 0);
					boolean datinhroi = false;
					for (int k = 0; k < listvitri.size(); k++) {
						if (listrectchar30.get(listvitri.get(k)).y
								+ listrectchar30.get(listvitri.get(k)).height / 2 > listrectchar30.get(i).y
								&& listrectchar30.get(listvitri.get(k)).y + listrectchar30.get(listvitri.get(k)).height
										/ 2 < listrectchar30.get(i).y + listrectchar30.get(i).height) {
							datinhroi = true;
							break;
						}
					}
					if (datinhroi) {
						datinhroi = false;
						continue;
					}
					datinhroi = false;

					for (int j = i + 1; j < listrectchar30.size(); j++) {
						if (listrectchar30.get(i).y + listrectchar30.get(i).height / 2 > listrectchar30.get(j).y
								&& listrectchar30.get(i).y + listrectchar30.get(i).height / 2 < listrectchar30.get(j).y
										+ listrectchar30.get(j).height
								&& (float) listrectchar30.get(i).height > listrectchar30.get(j).height * 0.9
								&& (float) listrectchar30.get(i).height < listrectchar30.get(j).height * 1.1) {
							demkytu++;
						}
					}
					if (demkytu >= 2) {
						listvitri.add(i);
					}
					demkytu = 1;
				}

				List<Integer> listvitri2 = new ArrayList<Integer>();
				for (int i = 0; i < listvitri.size(); i++) {
					for (int j = 0; j < listvitri.size(); j++) {
						if (i != j && (float) listrectchar30.get(i).height > listrectchar30.get(j).height * 0.9
								&& (float) listrectchar30.get(i).height < listrectchar30.get(j).height * 1.1) {
							listvitri2.add(i);
						}
					}
				}
				for (int i = 0; i < listvitri2.size(); i++) {
					Imgproc.rectangle(im, listrectchar30.get(listvitri.get(listvitri2.get(i))), new Scalar(0, 0, 255),
							1, 8, 0);
				}

				List<Rect> listrectchartop = new ArrayList<>();
				List<Rect> listrectcharbottom = new ArrayList<>();
				if (listvitri2.size() == 2) {
					// dong thu nhat
					for (int i = 0; i < listrectchar30.size(); i++) {
						if (listrectchar30.get(listvitri.get(listvitri2.get(0))).y
								+ listrectchar30.get(listvitri.get(listvitri2.get(0))).height
										/ 2 > listrectchar30.get(i).y
								&& listrectchar30.get(listvitri.get(listvitri2.get(0))).y
										+ listrectchar30.get(listvitri.get(listvitri2.get(0))).height
												/ 2 < listrectchar30.get(i).y + listrectchar30.get(i).height
								&& (float) listrectchar30.get(listvitri.get(listvitri2.get(0))).height > listrectchar30
										.get(i).height * 0.9
								&& (float) listrectchar30.get(listvitri.get(listvitri2.get(0))).height < listrectchar30
										.get(i).height * 1.1) {
							listrectcharbottom.add(listrectchar30.get(i));
						}
					}
					// dong thu 2
					for (int i = 0; i < listrectchar30.size(); i++) {
						if (listrectchar30.get(listvitri.get(listvitri2.get(1))).y
								+ listrectchar30.get(listvitri.get(listvitri2.get(1))).height
										/ 2 > listrectchar30.get(i).y
								&& listrectchar30.get(listvitri.get(listvitri2.get(1))).y
										+ listrectchar30.get(listvitri.get(listvitri2.get(1))).height
												/ 2 < listrectchar30.get(i).y + listrectchar30.get(i).height
								&& (float) listrectchar30.get(listvitri.get(listvitri2.get(1))).height > listrectchar30
										.get(i).height * 0.9
								&& (float) listrectchar30.get(listvitri.get(listvitri2.get(1))).height < listrectchar30
										.get(i).height * 1.1) {
							listrectchartop.add(listrectchar30.get(i));
						}
					}
				}

				// vị trí biển số
				int x = listrectchartop.get(0).x;
				int y = listrectchartop.get(0).y;
				;
				int width = 0;
				int height = 0;
				for (int i = 0; i < listrectchartop.size(); i++) {
					Imgproc.rectangle(im, listrectchartop.get(i), new Scalar(0, 255, 0), 1, 8, 0);
					if (x > listrectchartop.get(i).x) {
						x = listrectchartop.get(i).x;
					}
					if (y > listrectchartop.get(i).y) {
						y = listrectchartop.get(i).y;
					}
					if (x + width < listrectchartop.get(i).x + listrectchartop.get(i).width) {
						width = listrectchartop.get(i).x + listrectchartop.get(i).width - x;
					}
				}
				int widthhangtren = x + width;
				int vitrikytudau = listrectcharbottom.get(0).x;
				for (int i = 0; i < listrectcharbottom.size(); i++) {
					Imgproc.rectangle(im, listrectcharbottom.get(i), new Scalar(255, 0, 0), 1, 8, 0);
					if (x > listrectcharbottom.get(i).x) {
						x = listrectcharbottom.get(i).x;
					}
					if (y > listrectcharbottom.get(i).y) {
						y = listrectcharbottom.get(i).y;
					}
					if (x + width < listrectcharbottom.get(i).x + listrectcharbottom.get(i).width) {
						width = listrectcharbottom.get(i).x + listrectcharbottom.get(i).width - x;
					}
					if (y + height < listrectcharbottom.get(i).y + listrectcharbottom.get(i).height) {
						height = listrectcharbottom.get(i).y + listrectcharbottom.get(i).height - y;
					}
					if (vitrikytudau > listrectcharbottom.get(i).x) {
						vitrikytudau = listrectcharbottom.get(i).x;
					}
				}
				if (vitrikytudau > x) {
					if (x - width / 5 >= 0) {
						x = x - width / 5;
						width = width + width / 5;
					} else {
						x = 0;
						try {
							width = width + width / 5;
						} catch (Exception ex) {
							try {
								width = width + width / 7;
							} catch (Exception ex2) {
								try {
									width = width + width / 9;
								} catch (Exception ex3) {
								}
							}
						}
					}
				}
				System.out.println("x= " + x + " , y=" + y + " , width=" + width + " , height=" + height);
				HighGui.imshow("Anh goc", im);
				HighGui.imshow("Bien so", new Mat(image, new Rect(x, y, width, height)));
				HighGui.waitKey(0);
				HighGui.destroyAllWindows();
			}
		});
	}
}
