package org.example;

//task 2-3
//import org.apache.commons.lang3.StringUtils; for task 3
import org.example.utils.StringProcessor; //for task 6
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// task 5
import java.io.InputStream;
import java.util.Properties;

import java.util.Scanner;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("The app is running");

        //task 3
        System.out.print("Enter string: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        //String reversed = StringUtils.reverse(input); //for task 3
        //String capitalized = StringUtils.capitalize(input); //for task 3
        String reversed = StringProcessor.reverse(input); //for task 6
        String capitalized = StringProcessor.capitalize(input); //for task 6

        log.info("Initial string: {}", input);
        log.info("Reversed string: {}", reversed);
        log.info("String with first capital letter: {}", capitalized);

        System.out.println("Result reverse: " + reversed);
        System.out.println("Result capitalize: " + capitalized);

        //task 5
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("build-passport.properties")) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);

                log.info("build.user = {}", properties.getProperty("build.user"));
                log.info("build.os = {}", properties.getProperty("build.os"));
                log.info("build.java = {}", properties.getProperty("build.java"));
                log.info("build.time = {}", properties.getProperty("build.time"));
                log.info("build.message = {}", properties.getProperty("build.message"));
                log.info("build.number = {}", properties.getProperty("build.number"));
                log.info("build.gitHash = {}", properties.getProperty("build.gitHash"));

            } else {
                log.warn("build-passport.properties file was mot found");
            }
        } catch (Exception e) {
            log.error("Error reading build-passport.properties", e);
        }

        log.info("The app has shut down");
    }
}