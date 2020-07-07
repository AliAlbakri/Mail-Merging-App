package sample;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Controller {
    @FXML MenuItem loadContact;
    @FXML Button PDF;
    @FXML ComboBox tags;
    @FXML TextArea message;
    @FXML Button start;
    @FXML TableView ContactTable;
    @FXML MenuItem viewcontact, save;
    @FXML TextField subject;


    // a 2D arrayList for the contacts details
    ArrayList<ArrayList<String>> ContactList = new ArrayList<ArrayList<String>>();
    long number_of_Contacts;
    ObservableList<String> tags_list = FXCollections.observableArrayList();

    ArrayList<String> Merged_Message = new ArrayList<String>();

// loading contact file
    public void setLoadcontacts(){

        // open dialog
        FileChooser load_contacts = new FileChooser();
        load_contacts.setTitle("Load Contacts File");
        File thefile = load_contacts.showOpenDialog(new Stage());

        // enable some tools
        tags.setDisable(false);
        viewcontact.setDisable(false);
        message.setDisable(false);
        PDF.setDisable(false);
        start.setDisable(false);



        try {
            //  get the numbers of lines in contact file
            Path path = Paths.get(thefile.getAbsolutePath());
            number_of_Contacts = Files.lines(path).count()-1;

            Scanner input = new Scanner(new FileInputStream(thefile.getAbsolutePath()));

            StringTokenizer tag = new StringTokenizer(input.nextLine(),",");

            // adding the tags to the arraylist
            while (tag.hasMoreTokens()) {
                tags_list.add(tag.nextToken());

            }

            // if the file does not include email tag
            if (!tags_list.contains("[[EMAIL]]")){
                Alert missing_email = new Alert(Alert.AlertType.ERROR);
                missing_email.setTitle("missing Email ");
                missing_email.setContentText("Your file has no [[Email]] ");
                missing_email.show();
            }

            else {

                // adding the tags to combo box
                tags.setItems(tags_list);



            // adding the contact information to the 2d array list
            for (int i = 0; i < number_of_Contacts; i++) {
                    StringTokenizer info = new StringTokenizer(input.nextLine(), ",");
                    ContactList.add(new ArrayList<String>());
                    for (int j = 0; j < tags_list.size(); j++) {
                        ContactList.get(i).add(info.nextToken());
                    }
                }



            // adding tags to the message content
            tags.getSelectionModel().selectedItemProperty().addListener
                        ((v, oldValue, newValue) -> message.insertText(message.getCaretPosition(), (String) newValue));



            }

    } catch (IOException e) {
            e.printStackTrace();
        }

    }




    // process of generation messages
    public void Merging(){

        for (int j = 0;j<number_of_Contacts;j++) {
            String msg = message.getText();
            for (int i=0 ;i<tags_list.size();i++){
                // replaceing every tag with machining information in  contact array
                msg = msg.replace((String) tags_list.get(i), ContactList.get(j).get(i));
            }
            // adding the message after process to Array list
            Merged_Message.add(msg);
        }






    }

    String saved_path;

    //loading the template by the user
    public void load_template(){

        save.setDisable(false);

        // opening a file dialog
        FileChooser load_template = new FileChooser();
        load_template.setTitle("Load Contacts File");
        File thefile = load_template.showOpenDialog(new Stage());

        saved_path = thefile.getAbsolutePath();
        try {// putting the file details to the message area in the program
            Scanner input = new Scanner(new FileInputStream(thefile.getAbsolutePath()));

            String Template="";
            while (input.hasNext()){
               Template+= input.nextLine()+"\n";
            }
            message.setText(Template);

        }catch (IOException e){

        }

    }

    //saving the work written in the message area
    public void save() throws IOException{
        PrintWriter pw = new PrintWriter(new FileOutputStream(saved_path));
        pw.println(message.getText());
        pw.close();
        Alert saved = new Alert(Alert.AlertType.INFORMATION);
        saved.setTitle("Success");
        saved.setContentText("Your template has been edited successfully");
        saved.show();


    }

    //saving the template
    public void save_Template(){

        if (message.getText().equals("")){
            Alert empty_Message =  new Alert(Alert.AlertType.ERROR) ;
            empty_Message.setContentText("you cant save the file");
            empty_Message.setTitle("Empty content");
            empty_Message.show();
        }
        else {

            FileChooser savefile = new FileChooser();
            File selectfile = savefile.showSaveDialog(new Stage());
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(selectfile.getAbsolutePath()));
                pw.println(message.getText());
                pw.close();

                Alert saved = new Alert(Alert.AlertType.INFORMATION);
                saved.setTitle("Success");
                saved.setContentText("Your template has been saved successfully");
                saved.show();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    /**
     * @Author  Ali Albakri
     * @Author Omar Alwahhabie.
     * sendEmail methods handles the procces of sending email to the specified contacts selected by the user
     */

    public void sendEmail(){
        // preparing the messages
        Merging();
        //getting the index of emails to use them later on
        int mailIndex = tags_list.indexOf("[[EMAIL]]");

        //running the Password dialog for the user to enter the info.
        Platform.runLater(() -> {
            new PasswordDialog().start(new Stage());

            String userName = PasswordDialog.login.getUserName();
            String password = PasswordDialog.login.getPassword();




            Alert alert;

            SendEmailOffice365 sender ;
            boolean flag =true;


            for(int i =0;i<number_of_Contacts;i++) {
                sender = new SendEmailOffice365(userName, password, ContactList.get(i).get(mailIndex), subject.getText(), Merged_Message.get(i));
                if(sender.sendEmail()) {
                    continue;


                }
                else{

                    //updating the user if the password or the email was wrong

                    flag = false;
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login");
                    alert.setHeaderText("You have entered incorrect password");
                    alert.setContentText("Please try again");

                    alert.showAndWait();
                    break;
                }
            }



            if(flag) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("your messages have been sent successfully");
                alert.show();
            }


        });



    }


    // creating pdf file for the messages
    public void createPDF(){

        // empty message error
        if (message.getText().equals("")) {
            Alert empty = new Alert(Alert.AlertType.ERROR);
            empty.setTitle("Empty Content");
            empty.setContentText("you cant  create empty pdf file ");
            empty.show();
        }
        else {


            //preparing messages
            Merging();

            // passing the messages and number of contact as number of pages
            // to make pdf method in MailMerge class
            if (MailMerge.makePDF(Merged_Message, number_of_Contacts)) {
                Alert pdf_done = new Alert(Alert.AlertType.INFORMATION);
                pdf_done.setTitle("Success");
                pdf_done.setContentText("pdf file created successfully");
                pdf_done.show();
            }
        }
    }


    /**
     * @Author  Ali Albakri
     * @Author Omar Alwahhabie.
     * this method dispalyes the the the information of the contacts to user to conform their information before the mailing
     * process begins
     */
    public  void  viewContact()throws IOException{

       //creating a new Fxml loader
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Contactview.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));

        //Creating a ContactView instance to handle the showing of info
        Contactview view = fxmlLoader.getController();


        //sending the contact lists along with tags to preparing the displays of info.
        view.generateView(ContactList,tags_list);


        stage.show();



    }

}


