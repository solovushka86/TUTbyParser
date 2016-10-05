
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.sql.*;

/**
 * Created by HappyFamily on 13.09.2016.
 */
public class Parser {
    public static void main(String[] args) throws IOException {
        //JDBC connection to Database
        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("jdbc Driver not found");
        }
        Connection con = null;
        PreparedStatement stm = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vacancieslist", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Document doc = Jsoup.connect("https://jobs.tut.by/search/vacancy?text=&area=16").userAgent("Mozila").get(); //Create connection browser version of jobs.tut.by
//        Document doc = Jsoup.connect("https://jobs.tut.by/search/vacancy?text=&area=16").get(); // Create connection of mobile version jobs.tut.by
//        System.out.println(doc.html());
        System.out.println(doc.title());



        Elements classElements = doc.select("div.search-result-item__head");
        for(Element classElement : classElements) {
            Elements hrefElements = classElement.getElementsByTag("a");// select tag with a href
            String vacName = hrefElements.text();
            String link = hrefElements.attr("href"); // get string from tag with attr "href
            Document docVac = Jsoup.connect(link).userAgent("Mozila").get();

            //create elements for parsing CompanyName
            Elements companyName = docVac.select("div.companyname");
            String companyNameString = companyName.text();

            //create elements fo parsing salary
            Elements salary = docVac.select("td.l-content-colum-1.b-v-info-content");
            Elements salary1 = salary.select("div.l-paddings");
            String salaryString = salary1.text();

            //create element for parsing location
            Elements location = docVac.select("td.l-content-colum-2.b-v-info-content");
            Elements location1 = location.select("div.l-paddings");
            String locationString = location1.text();

            //create element for parsing experience
            Elements exp = docVac.select("td.l-content-colum-3.b-v-info-content");
            Elements exp1 = exp.select("div.l-paddings");
            String expString = exp1.text();

            String sqlInsert = "INSERT INTO vacancies (url, name, employer, salary, location, experience) VALUE (?,?,?,?,?,?)";
            try {
                stm = con.prepareStatement(sqlInsert);
                stm.setString(1,link);
                stm.setString(2,vacName);
                stm.setString(3, companyNameString);
                stm.setString(4, salaryString);
                stm.setString(5, locationString);
                stm.setString(6, expString);
                stm.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }





            System.out.println(link + " " + vacName + " " + companyNameString + " " + salaryString + " " +
                    locationString); // Print String with url vacancies to SOUT
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
