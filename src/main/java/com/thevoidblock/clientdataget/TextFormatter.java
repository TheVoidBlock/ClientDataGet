package com.thevoidblock.clientdataget;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {
    public static Text FormatNBTText(Text text) {
        /*
            Get the NBT list that we want to show.
            And we set the symbols up where we look for, so we can detect what to give which color.
         */
        String nbtList = String.valueOf(text.getString());
        Pattern p = Pattern.compile("[{}:\"\\[\\],']", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(nbtList);

        // Create new literalText, which we will be adding to the list.
        MutableText mutableText = Text.empty();

        /*  **Loop through the NBT data**
         *
         */
        // config
        Formatting stringColor = Color(ClientDataGetConfig.STRING_COLOR);
        Formatting quotationColor = Color(ClientDataGetConfig.QUOTATION_COLOR);
        Formatting separationColor = Color(ClientDataGetConfig.SEPARATION_COLOR);
        Formatting integerColor = Color(ClientDataGetConfig.INTEGER_COLOR);
        Formatting typeColor = Color(ClientDataGetConfig.TYPE_COLOR);
        Formatting fieldColor = Color(ClientDataGetConfig.FIELD_COLOR);

        int removedCharters = 0;
        int lastIndex = 0;
        Boolean singleQuotationMark = Boolean.FALSE;
        Boolean lineAdded = Boolean.FALSE;
        String lastString = "";


        while (m.find()) {
            lineAdded = Boolean.FALSE;

            /*  **Checking for single quotation marks**
             *  After that we check if the last char was a single quotation mark we can check that by checking the variable "singleQuotationMark".
             *  We do this so the data between the single quotation marks will get the "stringColor".
             *  And we make sure that the single quotation marks get the "quotationColor".
             */
            if (nbtList.charAt(m.start()) == '\'') {
                if (singleQuotationMark.equals(Boolean.FALSE)) { // If false color only the quotation mark
                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    singleQuotationMark = Boolean.TRUE;
                }
                else { // Else color the quotation mark and make the rest green
                    mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(stringColor));
                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    singleQuotationMark = Boolean.FALSE;
                }
                lastString = String.valueOf(nbtList.charAt(m.start()));
                lastIndex = m.start();
            }


            /*  **Checking if the text is not between the single quotation mark**
             *  1). When the text is not between the single quotation marks the normal formatting will get to work.
             *  2). We check if the char that is found is an opening bracket.
             *  3). The closing brackets and the comma (these are also the chars that decide when we go to the next line.).
             *  4). Now we check for the colon.
             *  5). And lastly we check for the double quotation marks.
             */
            if (singleQuotationMark == Boolean.FALSE) {

                /*  2). We check if the char that is found is an opening bracket.
                        Adds the found char and gives it the "separationColor"
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '{' || nbtList.charAt(m.start()) == '[' ) {
                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(separationColor));
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();
                }

                /*  3). The closing brackets and the comma (these are also the chars that decide when we go to the next line.).
                        If the char before the found char is a char that indicates what type of variable the text in front of it is.
                            Then: Add the text before the char that indicates the type and give it the "integerColor".
                                  After that add the char type and give it the "typeColor".
                            Else: Give all the text in front of the count char the "integerColor".
                        Now we add the found char with the "separationColor" (includes comma's)
                        If the char was a comma add a space (this way it's more readable)
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '}' || nbtList.charAt(m.start()) == ']' || nbtList.charAt(m.start()) == ',') {
                    if (nbtList.charAt(m.start()-1) == 's' || nbtList.charAt(m.start()-1) == 'S' ||
                            nbtList.charAt(m.start()-1) == 'b' || nbtList.charAt(m.start()-1) == 'B' ||
                            nbtList.charAt(m.start()-1) == 'l' || nbtList.charAt(m.start()-1) == 'L' ||
                            nbtList.charAt(m.start()-1) == 'f' || nbtList.charAt(m.start()-1) == 'F'
                    ) {
                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start()-1)).formatted(integerColor));
                        mutableText.append(Text.literal(nbtList.substring(m.start()-1,m.start())).formatted(typeColor));

                    }
                    else {
                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(integerColor));
                    }

                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColor);

                    if (nbtList.charAt(m.start()) == ',') { mutableText.append(Text.literal(" ").formatted(separationColor)); }
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();
                }

                /*  4). Now we check for the colon. (:)
                        If the last string doesn't equal the double quotation mark. (when it is between it should only get the "stringColor")
                            Then: Add the text in front of the colon and give it the "fieldColor".
                                  Add the found char and give it the "separationColor".
                                  Add a space so it's easier to read.
                                  Stores the lastString and lastIndex

                 */
                if (nbtList.charAt(m.start()) == ':') { // 4).
                    if (!lastString.equals("\"")) {
                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(fieldColor));

                        mutableText.append((Text.literal(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColor));
                        mutableText.append(Text.literal(" ").formatted(separationColor));
                        lastString = String.valueOf(nbtList.charAt(m.start()));
                        lastIndex = m.start();
                    }

                }
                /*  5). And lastly we check for the double quotation marks.
                        If the last char was a " too.
                            Then: If the string between the columns is longer then the linesStep
                                Then: Convert the string between the quotation marks to .... "lstringColor"
                                      And add the removed chars to the "removeCharters" variable.
                                Else: Add the string and give it the "stringColor"
                            Else: Only add the " since it's the first one.
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '"') {
                    if (lastString.equals("\"")){

                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(stringColor));

                        mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    }
                    else {
                        mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    }
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();

                }
            }
        }

        return mutableText;
    }

    private static Formatting Color(String color) {

        return switch (color) {
            case "black" -> Formatting.BLACK;
            case "dark_blue" -> Formatting.DARK_BLUE;
            case "dark_green" -> Formatting.DARK_GREEN;
            case "dark_aqua" -> Formatting.DARK_AQUA;
            case "dark_red" -> Formatting.DARK_RED;
            case "dark_purple" -> Formatting.DARK_PURPLE;
            case "gold" -> Formatting.GOLD;
            case "gray" -> Formatting.GRAY;
            case "dark_gray" -> Formatting.DARK_GRAY;
            case "blue" -> Formatting.BLUE;
            case "green" -> Formatting.GREEN;
            case "aqua" -> Formatting.AQUA;
            case "red" -> Formatting.RED;
            case "light_purple" -> Formatting.LIGHT_PURPLE;
            case "yellow" -> Formatting.YELLOW;
            default -> Formatting.WHITE;
        };
    }

}