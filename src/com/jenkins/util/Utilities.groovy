package com.jenkins.util

import java.util.Map

// all methods that need to be called from scripts under "vars" directory must be implemented here
@Singleton
public class Utilities {

    public static long startTime
    public static String endTime

    public static void printToConsoleOutput(Object scope, List input, String delimiter) {
        String output = ""
        for (List list : input) {
            String color = list[0]
            String message = list[1]
            switch(color) {
                case "RED": // Error
                    output += "\u001b[31;1m" + message + "\u001b[0m"
                    break
                case "CYAN": // Warn
                    output += "\u001b[36;1m" + message + "\u001b[0m"
                    break
                case "GREEN": // Success
                    output += "\u001b[32;1m" + message + "\u001b[0m"
                    break
                case "BLUE": // Info
                    output += "\u001b[34;1m" + message + "\u001b[0m"
                    break
                case "MAGENTA": // Info
                    output += "\u001b[35;1m" + message + "\u001b[0m"
                    break
                case "YELLOW": // Title
                    output += "\u001b[43;1m" + message + "\u001b[0m"
                    break
                case "WHITE": // Title
                    output += "\u001b[47;1m" + message + "\u001b[0m"
                    break
                default:
                    output += "\u001b[30;1m" + message + "\u001b[0m"
                    break
            }
            output += delimiter
        }
        scope.echo(output.substring(0, output.length() - delimiter.length()))
    }

    public static void printToConsoleOutput(Object scope, List input) {
        String color = input[0]
        String message = input[1]
        switch(color) {
            case "RED": // Error
                scope.echo("\u001b[31;1m" + message + "\u001b[0m")
                break
            case "CYAN": // Warn
                scope.echo("\u001b[36;1m" + message + "\u001b[0m")
                break
            case "GREEN": // Success
                scope.echo("\u001b[32;1m" + message + "\u001b[0m")
                break
            case "BLUE": // Info
                scope.echo("\u001b[34;1m" + message + "\u001b[0m")
                break
            case "MAGENTA": // Info
                scope.echo("\u001b[35;1m" + message + "\u001b[0m")
                break
            case "YELLOW": // Title
                scope.echo("\u001b[43;1m" + message + "\u001b[0m")
                break
            case "WHITE": // Title
                scope.echo("\u001b[47;1m" + message + "\u001b[0m")
                break
            default:
                scope.echo("\u001b[30;1m" + message + "\u001b[0m")
                break
        }
    }

    public static Map reconcileConfig(Map projectConfig, Map globalConfig) {
        if (projectConfig == null) {
            return globalConfig
        }
        else {
            // Applying D&D Rule
            Map config = globalConfig.clone()
            for( item in projectConfig) {
                config[item.key] = projectConfig[item.key]
            }
            return config
        }
    }

    public static boolean hasRequiredConfig(Object scope, Map actualConfig, ArrayList requiredConfig) {
        boolean hasRequiredConfig = true
        int size = requiredConfig.size()
        for (int index=0; index < size; index++) {
            if ( ! actualConfig.containsKey(requiredConfig[index]) ) {
                com.jenkins.util.Utilities.printToConsoleOutput(scope, [["RED", "Required parameter"],
                    ["CYAN", requiredConfig[index]], ["RED", "is not defined"]], " ")
                hasRequiredConfig = false
            }
        }
        return hasRequiredConfig
    }

    public static void startTimer() {
        this.startTime = System.currentTimeMillis()
    }

    public static void endTimer() {
        int duration = System.currentTimeMillis() - startTime
        //duration is in milliseconds
        duration /= (int) 1000
        //duration is in seconds
        int hours = duration / (int) 3600
        duration %= (int) 3600
        int minutes = duration / (int) 60
        duration %= (int) 60
        int seconds = (int) duration
        String durationString = ""
        if (hours > 0) {
            durationString += "${hours} hr(s) ${minutes} mins ${seconds} secs"
        }
        durationString += "${minutes} mins ${seconds} secs"
        this.endTime = durationString
    }

}