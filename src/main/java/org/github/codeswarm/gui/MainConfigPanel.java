package org.github.codeswarm.gui;

import com.sun.javafx.collections.ObservableSequentialListWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.github.codeswarm.CodeSwarm;
import org.github.codeswarm.ColorTest;
import org.github.codeswarm.Config;
import org.github.codeswarm.type.DisplayFile;

public class MainConfigPanel extends Application {

   public static void start() {
      launch(new String[]{});
   }

   private ChoiceBox<String> screenSize;

   private ColorPicker background;

   private ComboBox<String> fontType;

   private ComboBox<String> boldFontType;

   private ColorPicker fontColor;

   private ObservableList<ColorAssignerProperties> colorList = new ObservableSequentialListWrapper<>(new ArrayList<>());

   private ObservableList<String> fontList = new ObservableSequentialListWrapper<>(new ArrayList<>());

   private EditDialog editDialog;

   private final static String[] FONT_DEFAULTS = new String[] { "SansSerif", "Arial" }; 
   
   @Override
   public void start(Stage primaryStage) throws Exception {

      initialize();

      this.editDialog = new EditDialog(primaryStage);
      //primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/1f4d1.png")));
      primaryStage.setTitle("Code Swarm Configuration");
      primaryStage.setScene(mainScene());
      primaryStage.setResizable(true);
      primaryStage.show();
   }

   private void initialize() {
      this.colorList.clear();
      Config.getInstance().getColorAssigner().getTests().forEach((ct) -> {
         this.colorList.add(new ColorAssignerProperties(ct));
      });

      this.fontList.addAll(Font.getFontNames());

      // make a logical default
      Arrays.asList(FONT_DEFAULTS).forEach((font) -> {
         if (this.fontList.contains(font)) {
            Config.getInstance().setFont(font);
            Config.getInstance().setBoldFont(font);            
         }
      });
   }

   private Scene mainScene() {
      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(tabbedPane());
      borderPane.setBottom(buttonBar());

      StackPane root = new StackPane();
      root.getChildren().add(borderPane);

      return new Scene(root, 500, 400);
   }

   private TabPane tabbedPane() {
      TabPane tabPane = new TabPane();
      tabPane.getTabs().add(tabGeneral());
      tabPane.getTabs().add(tabColor());
      tabPane.getTabs().add(tabFiles());
      tabPane.getTabs().add(tabPerson());
      tabPane.getTabs().add(tabFileTypes());

      return tabPane;
   }

   private Tab tabGeneral() {
      Tab tab = createTab("General");
      GridPane gridPane = (GridPane) tab.getContent();

      Label screenSizeLbl = new Label("Screen size");
      GridPane.setHalignment(screenSizeLbl, HPos.RIGHT);
      gridPane.add(screenSizeLbl, 0, 1);

      this.screenSize = new ChoiceBox<>(
          FXCollections.observableArrayList("800x600", "960x720", "1024x768", "1920x1080"));
      GridPane.setHalignment(screenSize, HPos.LEFT);
      gridPane.add(screenSize, 1, 1);
      String current = Config.getInstance().getWidth().getValue() + "x" + Config.getInstance().getHeight().getValue();
      screenSize.getSelectionModel().select(current);

      Label framesPerDayLbl = new Label("Frames per day");
      GridPane.setHalignment(framesPerDayLbl, HPos.RIGHT);
      gridPane.add(framesPerDayLbl, 0, 2);

      TextField framesPerDay = new TextField();
      framesPerDay.setText("6");
      GridPane.setHalignment(framesPerDay, HPos.LEFT);
      gridPane.add(framesPerDay, 1, 2);

      CheckBox legend = new CheckBox("Show legend");
      legend.selectedProperty().bindBidirectional(Config.getInstance().getShowLegend());
      GridPane.setHalignment(legend, HPos.LEFT);
      gridPane.add(legend, 1, 3);

      CheckBox histogram = new CheckBox("Show histogram");
      histogram.selectedProperty().bindBidirectional(Config.getInstance().getShowHistogram());
      GridPane.setHalignment(histogram, HPos.LEFT);
      gridPane.add(histogram, 1, 4);

      CheckBox userName = new CheckBox("Show username");
      userName.selectedProperty().bindBidirectional(Config.getInstance().getShowUsername());
      GridPane.setHalignment(userName, HPos.LEFT);
      gridPane.add(userName, 1, 5);

      CheckBox popular = new CheckBox("Show popular");
      popular.selectedProperty().bindBidirectional(Config.getInstance().getShowPopular());
      GridPane.setHalignment(popular, HPos.LEFT);
      gridPane.add(popular, 1, 6);

      CheckBox date = new CheckBox("Show date");
      date.selectedProperty().bindBidirectional(Config.getInstance().getShowDate());
      GridPane.setHalignment(date, HPos.LEFT);
      gridPane.add(date, 1, 7);

      CheckBox edges = new CheckBox("Show edges");
      edges.selectedProperty().bindBidirectional(Config.getInstance().getShowEdges());
      GridPane.setHalignment(edges, HPos.LEFT);
      gridPane.add(edges, 1, 8);

      return tab;
   }

   private Tab tabFiles() {
      Tab tab = createTab("File");
      GridPane gridPane = (GridPane) tab.getContent();

      Label drawFileLbl = new Label("Draw file");
      GridPane.setHalignment(drawFileLbl, HPos.RIGHT);
      gridPane.add(drawFileLbl, 0, 1);

      ChoiceBox<DisplayFile> displayFile = new ChoiceBox<>();
      displayFile.setConverter(new StringConverter<DisplayFile>() {
         @Override
         public String toString(DisplayFile object) {
            return object.getLabel();
         }

         @Override
         public DisplayFile fromString(String string) {
            // not used
            return DisplayFile.FUZZY;
         }
      });
      displayFile.getItems().addAll(DisplayFile.values());
      displayFile.setOnAction((event) -> {
         Config.getInstance().setDisplayFile(displayFile.getValue());
      });
      displayFile.setValue(Config.getInstance().getDisplayFile());
      GridPane.setHalignment(displayFile, HPos.LEFT);
      gridPane.add(displayFile, 1, 1);

      return tab;
   }

   private Tab tabPerson() {
      Tab tab = createTab("Person");
      GridPane gridPane = (GridPane) tab.getContent();

      CheckBox drawHalo = new CheckBox("Draw halos");
      drawHalo.selectedProperty().bindBidirectional(Config.getInstance().getDrawNamesHalo());
      GridPane.setHalignment(drawHalo, HPos.LEFT);
      gridPane.add(drawHalo, 1, 0);
      
      CheckBox drawSharp = new CheckBox("Draw sharp");
      drawSharp.selectedProperty().bindBidirectional(Config.getInstance().getDrawNamesSharp());
      GridPane.setHalignment(drawSharp, HPos.LEFT);
      gridPane.add(drawSharp, 1, 1);

      return tab;
   }
   
   private Tab tabColor() {
      Tab tab = createTab("Color");
      GridPane gridPane = (GridPane) tab.getContent();

      Label backgroundLbl = new Label("Background color");
      GridPane.setHalignment(backgroundLbl, HPos.RIGHT);
      gridPane.add(backgroundLbl, 0, 1);

      this.background = new ColorPicker();
      this.background.valueProperty().bindBidirectional(Config.getInstance().getBackground());
      GridPane.setHalignment(background, HPos.LEFT);
      gridPane.add(background, 1, 1);

      Label fontTypeLbl = new Label("Font");
      GridPane.setHalignment(fontTypeLbl, HPos.RIGHT);
      gridPane.add(fontTypeLbl, 0, 2);

      this.fontType = new ComboBox<>();
      fontType.setItems(this.fontList);
      fontType.getSelectionModel().select(Config.getInstance().getFont());
      fontType.setOnAction((event) -> {
         Config.getInstance().setFont(fontType.getSelectionModel().getSelectedItem());
      });
      GridPane.setHalignment(fontType, HPos.LEFT);
      gridPane.add(fontType, 1, 2);

      Label fontColorLbl = new Label("Font color");
      GridPane.setHalignment(fontColorLbl, HPos.RIGHT);
      gridPane.add(fontColorLbl, 0, 3);

      this.fontColor = new ColorPicker();
      this.fontColor.valueProperty().bindBidirectional(Config.getInstance().getFontColor());
      GridPane.setHalignment(fontColor, HPos.LEFT);
      gridPane.add(fontColor, 1, 3);

      Label fontSizeLbl = new Label("Font size");
      GridPane.setHalignment(fontSizeLbl, HPos.RIGHT);
      gridPane.add(fontSizeLbl, 0, 4);

      TextField fontSize = new TextField();
      fontSize.setTextFormatter(new TextFormatter<Integer>(new StringConverter<Integer>() {
         @Override
         public String toString(Integer object) {
            if (object == null) {
               return "0";
            }
            return object.toString();
         }

         @Override
         public Integer fromString(String string) {
            return Integer.parseInt(string);
         }
      }));
      fontSize.textProperty().bindBidirectional(Config.getInstance().getFontSize(), new NumberStringConverter());
      GridPane.setHalignment(fontSize, HPos.LEFT);
      gridPane.add(fontSize, 1, 4);

      Label boldFontTypeLbl = new Label("Bold font");
      GridPane.setHalignment(boldFontTypeLbl, HPos.RIGHT);
      gridPane.add(boldFontTypeLbl, 0, 5);

      this.boldFontType = new ComboBox<>();
      boldFontType.setItems(this.fontList);
      boldFontType.getSelectionModel().select(Config.getInstance().getBoldFont());
      boldFontType.setOnAction((event) -> {
         Config.getInstance().setBoldFont(boldFontType.getSelectionModel().getSelectedItem());
      });
      GridPane.setHalignment(boldFontType, HPos.LEFT);
      gridPane.add(boldFontType, 1, 5);

      Label boldFontSizeLbl = new Label("Bold font size");
      GridPane.setHalignment(boldFontSizeLbl, HPos.RIGHT);
      gridPane.add(boldFontSizeLbl, 0, 6);
      
      TextField boldFontSize = new TextField();
      boldFontSize.setTextFormatter(new TextFormatter<Integer>(new StringConverter<Integer>() {
         @Override
         public String toString(Integer object) {
            if (object == null) {
               return "0";
            }
            return object.toString();
         }

         @Override
         public Integer fromString(String string) {
            return Integer.parseInt(string);
         }
      }));
      boldFontSize.textProperty().bindBidirectional(Config.getInstance().getBoldFontSize(), new NumberStringConverter());
      GridPane.setHalignment(boldFontSize, HPos.LEFT);
      gridPane.add(boldFontSize, 1, 6);
      
      return tab;
   }

   private Tab createTab(String title) {
      Tab tab = new Tab();
      tab.setClosable(false);
      tab.setText(title);

      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(5d));
      gridPane.setVgap(5d);
      gridPane.setHgap(5d);

      ColumnConstraints column1 = new ColumnConstraints(125);
      ColumnConstraints column2 = new ColumnConstraints(50, 75, 150);
      column2.setHgrow(Priority.ALWAYS);
      gridPane.getColumnConstraints().addAll(column1, column2);
      tab.setContent(gridPane);

      return tab;
   }

   private Tab tabFileTypes() {
      Tab tab = new Tab();
      tab.setClosable(false);
      tab.setText("Filetype");

      BorderPane borderPane = new BorderPane();
      borderPane.setPadding(new Insets(5d));
      tab.setContent(borderPane);

      VBox vBox = new VBox(5d);
      vBox.setPadding(new Insets(5d));
      vBox.setMinWidth(75d);

      TableView<ColorAssignerProperties> tableView = new TableView<>();

      Button addButton = new Button("Add");
      addButton.setOnAction((event) -> {
         ColorAssignerProperties cap = new ColorAssignerProperties(new ColorTest());
         this.editDialog.createDialog(cap).showAndWait();
         this.colorList.add(0, cap);
      });
      addButton.setMinWidth(75d);
      vBox.getChildren().add(addButton);

      Button editButton = new Button("Edit");
      editButton.setOnAction((event) -> {
         this.editDialog.createDialog(tableView.getSelectionModel().getSelectedItem()).showAndWait();
      });
      editButton.setMinWidth(75d);
      vBox.getChildren().add(editButton);

      Button removeButton = new Button("Remove");
      removeButton.setMinWidth(75d);
      vBox.getChildren().add(removeButton);

      borderPane.setRight(vBox);

      tableView.setItems(colorList);
      tableView.getSelectionModel().select(0);
      tableView.setEditable(true);
      borderPane.setCenter(tableView);

      TableColumn<ColorAssignerProperties, String> labelCol = new TableColumn<>("Label");
      TableColumn<ColorAssignerProperties, String> patternCol = new TableColumn<>("Pattern");
      TableColumn<ColorAssignerProperties, Color> colorCol = new TableColumn<>("Color");

      labelCol.setMinWidth(100d);
      patternCol.setMinWidth(100d);
      colorCol.setMinWidth(80d);

      tableView.getColumns().add(labelCol);
      tableView.getColumns().add(patternCol);
      tableView.getColumns().add(colorCol);
      tableView.setMaxWidth(290d);

      labelCol.setCellValueFactory(c -> c.getValue().getLabel());
      patternCol.setCellValueFactory(c -> c.getValue().getExpression());
      colorCol.setCellValueFactory(c -> c.getValue().getColor());
      colorCol.setCellFactory((param) -> {
         return new TableCell<ColorAssignerProperties, Color>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
               super.updateItem(item, empty);

               if (item != null && !empty) {
                  setText(item.toString());
                  setTextFill(item);
                  setBackground(new Background(new BackgroundFill(background.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
               }
            }
         };
      });

      return tab;
   }

   private Node buttonBar() {
      HBox hBox = new HBox(5d);
      hBox.setPadding(new Insets(5d));
      hBox.setAlignment(Pos.CENTER_RIGHT);

      Button preview = new Button("Preview");
      preview.setOnAction(a -> {
         setConfig();
         CodeSwarm.boot();
      });
      hBox.getChildren().add(preview);
      hBox.getChildren().add(new Button("Start"));

      Button quit = new Button("Quit");
      quit.setOnAction(a -> {
         System.exit(0);
      });
      hBox.getChildren().add(quit);

      return hBox;
   }

   private void setConfig() {
      Config.getInstance().getColorAssigner().getTests().clear();
      this.colorList.forEach((ct) -> {
         Config.getInstance().getColorAssigner().addRule(ct.getLabel().getValue(), ct.getExpression().getValue(), ColorUtil.toAwtColor(ct.getColor().getValue()));
      });

      String screenSize = this.screenSize.getSelectionModel().getSelectedItem();
      Config.getInstance().setWidth(Integer.parseInt(screenSize.split("x")[0]));
      Config.getInstance().setHeight(Integer.parseInt(screenSize.split("x")[1]));
   }
}
