package controller;

import com.jfoenix.controls.JFXButton;
import db.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import view.tdm.CustomerTM;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * @author : Sanu Vithanage
 * @since : 0.1.0
 **/
public class CustomerFormController implements Initializable {


    public TextField txtCusID;
    public TextField txtCusName;
    public TextField txtCusAddress;
    public TextField txtCusSalary;
    public TableView<CustomerTM> tblCustomer;
    public JFXButton btnSaveCustomer;
    public JFXButton btnHelloJasper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
    }

    private void initTable() {
        /*initialize table column values*/
        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        /*set two images to the last columns*/
        tblCustomer.getColumns().get(3).setCellValueFactory((param) -> {
            ImageView edit = new ImageView("/view/assets/icons/draw.png");
            ImageView delete = new ImageView("/view/assets/icons/trash.png");

            return new ReadOnlyObjectWrapper(new HBox(10, edit, delete));
        });
    }


    public void helloJasperEvent(MouseEvent event) {
        try {
            /*01-Lets catch the report file*/
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("/view/reports/WelcomeReport.jrxml"));

            /*02- Lets compile the jasper design */
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            /*03- Set the resources for the compiled report*/
            /*1- Complied report*/
            /*2- Parameters = null*/
            /*2- DataSource = JREmpty Data Source*/
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, new JREmptyDataSource(1));

            /*04- Lets view the report*/
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }


    public void generateSQLReport(MouseEvent event) {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("/view/reports/SQL_Report.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sqlChartEvent(MouseEvent event) {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("/view/reports/SQL_Chart.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);
            /*Here we can set the DBConnection to the data source because this report use mysql to get data*/
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomerEvent(MouseEvent event) {
        /*Add Customer*/
        /*01. Gather Details*/
        String customerID = txtCusID.getText();
        String customerName = txtCusName.getText();
        String customerAddress = txtCusAddress.getText();
        String customerSalary = txtCusSalary.getText();

        /*Fill those values to the Table Model before adding to the table*/
        CustomerTM customerTM = new CustomerTM(customerID, customerName, customerAddress, new BigDecimal(customerSalary));

        /*Get the table and add the table model*/
        tblCustomer.getItems().add(customerTM);


    }

    public void beanArrayEvent(MouseEvent event) {
        try {
            /*Create a report using table data*/
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("/view/reports/BeanArrayReport.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);
            /*Get all values from table*/
            ObservableList<CustomerTM> items = tblCustomer.getItems();
            /*Create a Bean Array Data Source and pass the table values to it*/
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, new JRBeanArrayDataSource(items.toArray()));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void reportWithParam(MouseEvent event) {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("/view/reports/ReportWithParams_Beans.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);
            /*Get all values from table*/
            ObservableList<CustomerTM> items = tblCustomer.getItems();
            /*Create a Bean Array Data Source and pass the table values to it*/

            /*setting values for parameters*/
            String customerID = txtCusID.getText();
            String customerName = txtCusName.getText();
            String customerAddress = txtCusAddress.getText();

            /*Setting parameter values*/
            HashMap map = new HashMap();
            map.put("id", customerID);// id= param name of report // customerID= input value of text field
            map.put("name", customerName);
            map.put("address", customerAddress);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(items.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

            //if you wanna print the report directly you can use this instead of JasperViewer
            /*JasperPrintManager.printReport(jasperPrint,false);*/

        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    public void sqlAndParamEvent(MouseEvent event) {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("/view/reports/PramsAndSQL.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            /*Setting values for parameters*/
            /*Get the customer id input field value*/
            String customerID = txtCusID.getText();

            /*Setting parameter values for the report*/
            HashMap map = new HashMap();
            map.put("searchID", customerID);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);

            /*JasperPrintManager.printReport(jasperPrint,false);*/

        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
