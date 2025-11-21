package view.frame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

public class ImageCrop extends JPanel {
    private BufferedImage image;
    private double scale = 1.0;
    private int imageX = 0, imageY = 0; 
    private Rectangle viewArea; 
    private JFrame frame; 
    private Point dragStartPoint;
    private BufferedImage clippedImage;
    private Consumer<BufferedImage> onImageClipped;
    public ImageCrop(BufferedImage image, Consumer<BufferedImage> onImageClipped) {
        this.onImageClipped = onImageClipped;
        BufferedImage resizedImage = resizeToFit(image, 400); // targetSize에 맞춰 이미지 크기 조정
        this.image = resizedImage;

        // 보이는 영역을 400x400으로 설정
        this.viewArea = new Rectangle(0, 0, 400, 400);
        setPreferredSize(new Dimension(400, 400)); // 패널 크기를 400x400으로 설정

        // 이미지를 0,0 위치로 고정 (좌상단)
        imageX = 0;
        imageY = 0;

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();

                // 마우스의 현재 위치
                Point mousePoint = e.getPoint();

                // 현재 마우스 위치에 해당하는 이미지 상의 좌표
                double imgXRelativeToMouse = (mousePoint.x - imageX) / scale;
                double imgYRelativeToMouse = (mousePoint.y - imageY) / scale;

                // 이미지의 확대/축소
                if (notches < 0) {
                    scaleUp(scale);
                } else {
                    scaleDown(scale);
                }

                // 확대/축소 후에도 마우스 위치가 동일한 이미지를 가리키도록 좌표를 조정
                imageX = (int) (mousePoint.x - imgXRelativeToMouse * scale);
                imageY = (int) (mousePoint.y - imgYRelativeToMouse * scale);

                // 이미지 경계 제한
                adjustImagePosition(resizedImage);

                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point dragEndPoint = e.getPoint();
                int deltaX = dragEndPoint.x - dragStartPoint.x;
                int deltaY = dragEndPoint.y - dragStartPoint.y;

                System.out.println(imageX+"\t"+ imageY+"\t"+resizedImage.getWidth() 
                +"\t"+resizedImage.getHeight()+"\t"+
                        (int)(image.getWidth() - image.getWidth()*scale)
                        +"\t"+(int)(image.getHeight() - image.getHeight()*scale)+"\t"+scale);
                if (imageX > 0) {
                    imageX = 0;


                } else if (imageY > 0) {
                    imageY = 0;

                } else if(imageX < 400-resizedImage.getWidth()*scale){
                    imageX = (int) (400-resizedImage.getWidth()*scale);
                } else if (imageY < 400-resizedImage.getHeight()*scale) {
                    imageY = (int) (400-resizedImage.getHeight()*scale);
                } else{
                    imageX +=deltaX;
                    imageY +=deltaY;
                }



                dragStartPoint = dragEndPoint;
                repaint();
            }
        });



        // JFrame 설정 및 구성
        frame = new JFrame("Image Clipper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // 버튼 패널을 생성
        // 클리핑된 이미지를 저장하는 버튼
        JPanel buttonPanel = new JPanel();
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");
        JButton confirmButton  = new JButton("확인");
        JButton cancelButton = new JButton("취소");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clippedImage = clipImage(); // 클리핑된 이미지 생성
                onImageClipped.accept(clippedImage);  // 콜백 실행
                frame.dispose();  // 프레임 닫기
            }
        });
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleUp(scale);
                repaint();
            }
        });

        // 축소 버튼 (500x500 이하로는 축소 불가능)
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaleDown(scale);
                repaint();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
            }
        });
        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // JSplitPane 생성: 상단에 이미지, 하단에 버튼
        JScrollPane scrollPane = new JScrollPane(this);
        // 스크롤바를 비활성화
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, buttonPanel);
        splitPane.setDividerLocation(400);  // 이미지 영역 크기를 400으로 설정
        splitPane.setResizeWeight(1.0); // 상단 영역을 고정 크기로 설정

        // SplitPane을 프레임에 추가
        frame.getContentPane().add(splitPane);
        frame.setSize(400, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static BufferedImage resizeToFit(BufferedImage originalImage, int targetSize) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 더 큰 쪽을 targetSize에 맞추고 비율에 맞춰 다른 쪽을 조정 (max 사용)
        double scaleFactor = Math.max((double) targetSize / originalWidth, (double) targetSize / originalHeight);

        int targetWidth = (int) (originalWidth * scaleFactor);
        int targetHeight = (int) (originalHeight * scaleFactor);

        return resizeImage(originalImage, targetWidth, targetHeight);
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 이미지를 스케일 및 이동 좌표를 적용하여 0,0 위치에서 그리기
        g2d.translate(imageX, imageY);
        g2d.scale(scale, scale);
        g2d.drawImage(image, 0, 0, this);
    }

    public void saveClippedImage(String outputPath) {
        // 현재 화면에 보이는 확대/축소된 이미지 영역을 계산
        int clippedX = (int) ((viewArea.x - imageX) / scale);
        int clippedY = (int) ((viewArea.y - imageY) / scale);
        int clippedWidth = (int) (viewArea.width / scale);
        int clippedHeight = (int) (viewArea.height / scale);

        // 잘라내는 영역이 이미지 경계를 넘지 않도록 제한
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        clippedX = Math.max(clippedX, 0);
        clippedY = Math.max(clippedY, 0);
        clippedWidth = Math.min(clippedWidth, imageWidth - clippedX);
        clippedHeight = Math.min(clippedHeight, imageHeight - clippedY);

        // 계산된 영역으로 이미지 클리핑
        BufferedImage clippedImage = image.getSubimage(clippedX, clippedY, clippedWidth, clippedHeight);
        try {
            // 잘라낸 이미지를 저장
            ImageIO.write(clippedImage, "png", new File(outputPath));
            System.out.println("클리핑된 이미지가 저장되었습니다: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private BufferedImage clipImage() {
        int clippedX = (int) ((viewArea.x - imageX) / scale);
        int clippedY = (int) ((viewArea.y - imageY) / scale);
        int clippedWidth = (int) (viewArea.width / scale);
        int clippedHeight = (int) (viewArea.height / scale);

        // 잘라내는 영역이 이미지 경계를 넘지 않도록 제한
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        clippedX = Math.max(clippedX, 0);
        clippedY = Math.max(clippedY, 0);
        clippedWidth = Math.min(clippedWidth, imageWidth - clippedX);
        clippedHeight = Math.min(clippedHeight, imageHeight - clippedY);

        return image.getSubimage(clippedX, clippedY, clippedWidth, clippedHeight);
    }

    public void scaleUp(double scale){
        if (scale < 2.5) {
            scale = scale *= 1.1; // 확대
            this.scale = scale;
        }
    }
    private void adjustImagePosition(BufferedImage resizedImage) {
        int scaledImageWidth = (int) (resizedImage.getWidth() * scale);
        int scaledImageHeight = (int) (resizedImage.getHeight() * scale);
        System.out.println(image.getWidth() - scaledImageWidth+"\t"+(int)(image.getHeight() - scaledImageHeight));
        // 이미지가 왼쪽이나 위쪽 경계를 벗어나는 것을 방지
        if (imageX > 0) {
            imageX = 0; // 이미지가 좌측 경계를 벗어나지 않도록
        } else if (imageY > 0) {
            imageY = 0; // 이미지가 상단 경계를 벗어나지 않도록
        }

        if (imageX < 400 - scaledImageWidth) {
            imageX = 400- scaledImageWidth; // 이미지가 우측 경계를 벗어나지 않도록
        } else if (imageY < 400 - scaledImageHeight) {
            imageY = 400 - scaledImageHeight; // 이미지가 하단 경계를 벗어나지 않도록
        }
        // 이미지가 오른쪽이나 아래쪽 경계를 벗어나는 것을 방지
    }


    public void scaleDown(double scale){
        if (scale > 1.0) {
            scale = scale /= 1.1; //축소
            this.scale = scale;
        }
    }
}

