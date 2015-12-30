package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;

public class ElementTextInputNumber extends ElementTextInput
{
    public boolean allowDecimal;

    public ElementTextInputNumber(Window window, int x, int y, int w, int h, int ID, String tip, boolean allowDec)
    {
        super(window, x, y, w, h, ID, tip, 60);
        allowDecimal = allowDec;
    }

    public ElementTextInputNumber(Window window, int x, int y, int w, int h, int ID, String tip, String curText, boolean allowDec)
    {
        super(window, x, y, w, h, ID, tip, 60, curText);
        allowDecimal = allowDec;
    }

    @Override
    public void checkAndCorrectText(String oldText)//Text needs to be corrected cause this becomes a field name in the future
    {
        String newString = textField.getText();
        if(newString.isEmpty() || newString.equals("-") || allowDecimal && newString.equals("."))
        {
            return;
        }
        boolean reject = true;
        try
        {
            if(allowDecimal)
            {
                Double.parseDouble(newString);
            }
            else
            {
                Integer.parseInt(newString);
            }
            reject = false;
        }
        catch(NumberFormatException ignored){}
        if(reject)
        {
            int pos = textField.getCursorPosition();
            textField.setText(oldText);
            textField.setCursorPosition(pos - 1);
        }
    }
}
