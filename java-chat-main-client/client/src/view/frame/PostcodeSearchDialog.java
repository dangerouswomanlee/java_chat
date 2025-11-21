package view.frame; 

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

// 콜백 인터페이스
interface AddressSelectedCallback {
    void onAddressSelected(String postcode, String address);
}

// JDialog 클래스
public class PostcodeSearchDialog extends JDialog {
    private JFXPanel jfxPanel;
    private AddressSelectedCallback callback;
    
    private JavaBridge bridge;
    public class JavaBridge {
        public void addressSelected(String postcode, String address) {
            SwingUtilities.invokeLater(() -> {
                if (callback != null) {
                    callback.onAddressSelected(postcode, address);
                }
                PostcodeSearchDialog.this.dispose();
            });
        }
    }

    // 생성자
    public PostcodeSearchDialog(Frame owner, AddressSelectedCallback callback) {
        super(owner, "우편번호 검색", false); 
        this.callback = callback;
        this.bridge = new JavaBridge();
        
        jfxPanel = new JFXPanel();
        Platform.runLater(this::createJFXScene); 
        add(jfxPanel, BorderLayout.CENTER);
        setSize(500, 500);
        setLocationRelativeTo(owner);
    }

    private void createJFXScene() {
        WebView webView = new WebView();

        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject jsObject = (JSObject) webView.getEngine().executeScript("window");
            
                jsObject.setMember("javaBridge", this.bridge);

                try {
                    webView.getEngine().executeScript("window.bridgeIsReady = true;");
                    System.out.println(">>> [JavaFX] Sent bridgeIsReady = true signal to JavaScript.");
                } catch (Exception e) {
                     System.err.println(">>> [JavaFX] Error sending bridgeIsReady signal: " + e.getMessage());
                }
            }
        });
        
        // postcode.html 파일을 로드
        try {
            URL url = getClass().getResource("postcode.html");
            if (url == null) {
                throw new java.io.FileNotFoundException("오류: postcode.html 파일을 view/frame 폴더에서 찾을 수 없습니다.");
            }
            webView.getEngine().load(url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                jfxPanel.add(new JLabel(e.getMessage()));
            });
        }

        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);
    }
}