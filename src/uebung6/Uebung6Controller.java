package uebung6;

import java.io.File;

import uebung6.Prediction.FilterType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class Uebung6Controller {

    private static final String initialFileName = "rsc/test1.jpg";
    private static File fileOpenPath = new File(".");

    private static final Prediction filter = new Prediction();

    @FXML
    private Slider quantizationSlider;

    @FXML
    private Label quantizationLabel;

    @FXML
    private ComboBox<FilterType> filterSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView predictionImageView;

    @FXML
    private ImageView reconstructedImageView;

    @FXML
    private Label messageLabel;

    @FXML
    private Label entropyOrginal;

    @FXML
    private Label entropyPredict;

    @FXML
    private Label entropyReconstruct;

    @FXML
    private Label MSE;

    @FXML
    void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(fileOpenPath);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null) {
            fileOpenPath = selectedFile.getParentFile();

//            new RasterImage(selectedFile).convertToGray();
//            new RasterImage(selectedFile).setToView(originalImageView);

            RasterImage img = new RasterImage(selectedFile);
            img.convertToGray();
            img.setToView(originalImageView);

            processImages();
            messageLabel.getScene().getWindow().sizeToScene();;
        }
    }

    @FXML
    void quantizationChanged() {
        int quantization = (int) quantizationSlider.getValue();
        quantizationLabel.setText("" + quantization);
        processImages();
    }

    @FXML
    void filterChanged() {
        processImages();
    }

    @FXML
    public void initialize() {
        // set combo boxes items
        filterSelection.getItems().addAll(FilterType.values());
        filterSelection.setValue(FilterType.A);

        // initialize parameters
        quantizationChanged();

        // load and process default image
//        new RasterImage(new File(initialFileName)).convertToGray();
//        new RasterImage(new File(initialFileName)).setToView(originalImageView);

        RasterImage img = new RasterImage(new File(initialFileName));
        img.convertToGray();
        img.setToView(originalImageView);

        processImages();
    }

    @FXML
    void reset(){
        filterSelection.setValue(FilterType.A);
        quantizationSlider.setValue(1);
        quantizationChanged();
    }

    private void processImages() {
        if(originalImageView.getImage() == null)
            return; // no image: nothing to do

        long startTime = System.currentTimeMillis();

        RasterImage origImg = new RasterImage(originalImageView);
//        origImg.convertToGray();
//        origImg.setToView(originalImageView);
        RasterImage predictionIMG = new RasterImage(origImg.width, origImg.height);
        RasterImage reconstructIMG = new RasterImage(origImg.width, origImg.height);

//        filter.copy(origImg, predictionIMG);
//        filter.copy(predictionIMG, reconstructIMG);
//        predictionIMG.convertToGray();
//        reconstructIMG.convertToGray();

        switch(filterSelection.getValue()) {
            case A:
                Prediction.caseA(origImg, predictionIMG, reconstructIMG, (float)quantizationSlider.getValue());
                break;
            case B:
                Prediction.caseB(origImg, predictionIMG, reconstructIMG, (float)quantizationSlider.getValue());
                break;
            case C:
                Prediction.caseC(origImg, predictionIMG, reconstructIMG, (float)quantizationSlider.getValue());
                break;
            case ABC:
                Prediction.caseABC(origImg, predictionIMG, reconstructIMG, (float)quantizationSlider.getValue());
                break;
            case ADAPTIV:
                Prediction.caseAdaptiv(origImg, predictionIMG, reconstructIMG, (float)quantizationSlider.getValue());
                break;

                default:
                break;
        }

        predictionIMG.setToView(predictionImageView);
        reconstructIMG.setToView(reconstructedImageView);

        entropyOrginal.setText(String.format("%.2f", Prediction.entropy(origImg)));
        entropyPredict.setText(String.format("%.2f", Prediction.entropy(predictionIMG)));
        entropyReconstruct.setText(String.format("%.2f", Prediction.entropy(reconstructIMG)));
        //MSE.setText("" + Prediction.getMSE());
        MSE.setText(String.format("%.2f", Prediction.getMSE()));
        messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
    }


}
