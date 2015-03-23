package me.dylanhart.calc;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import me.dylanhart.calc.core.ExpressionEvaluator;
import me.dylanhart.calc.core.ExpressionParser;
import me.dylanhart.calc.core.Token;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Queue;

public class Calc extends Activity {

    private EditText display;
    private TextView errorText;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        display = ((EditText) findViewById(R.id.display));
        display.setKeyListener(null);

        errorText = ((TextView) findViewById(R.id.errorText));

        genKeypad((TableLayout) findViewById(R.id.keypadLayout));
    }

    private void genKeypad(TableLayout tableLayout) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        TableRow currentRow = null;
        Button currentBtn = null;

        try {
            XmlResourceParser xrp = this.getResources().getXml(R.xml.keypad);

            while (xrp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlPullParser.START_TAG) {
                    String tag = xrp.getName();

                    if (tag.equals("row")) {
                        currentRow = (TableRow) inflater.inflate(R.layout.keypad_row, null);
                    } else if (tag.equals("btn")) {
                        String txt = xrp.getAttributeValue(null, "txt");
                        String action = xrp.getAttributeValue(null, "action");
                        boolean openParen = xrp.getAttributeBooleanValue(null, "openParen", false);
                        int drawable = xrp.getAttributeResourceValue(null, "drawable", R.drawable.button_bg);

                        currentBtn = (Button) inflater.inflate(R.layout.keypad_button, null);

                        currentBtn.setBackgroundResource(drawable);

                        if (txt != null) {
                            currentBtn.setText(txt);
                            currentBtn.setWidth(0);
                            if (action == null) {
                                if (openParen) {
                                    currentBtn.setOnClickListener(new KeypadClickListener(txt + "("));
                                } else {
                                    currentBtn.setOnClickListener(new KeypadClickListener(txt));
                                }
                            } else if (action.equals("delete")) {
                                currentBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int len = display.getText().length();
                                        if (len == 0) return;
                                        display.getText().delete(len - 1, len);
                                    }
                                });
                            } else if (action.equals("submit")) {
                                currentBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ExpressionParser ep = new ExpressionParser();
                                        ExpressionEvaluator ee = new ExpressionEvaluator();

                                        Queue<Token> expression = ep.read(display.getText().toString());

                                        Log.d("calc", (expression == null) ? "null" : expression.toString());

                                        if (expression == null) {
                                            errorText.setLines(1);
                                            errorText.setText(ep.getError());
                                        } else {
                                            errorText.setLines(0);
                                            display.setText(ee.eval(expression).toString());
                                        }
                                    }
                                });
                            } else if (action.equals("clear")) {
                                currentBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        display.getText().clear();
                                        errorText.setLines(0);
                                    }
                                });
                            } else if (action.equals("disabled")) {
                                currentBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getApplicationContext(), "disabled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                } else if (xrp.getEventType() == XmlPullParser.END_TAG) {
                    String tag = xrp.getName();
                    if (tag.equals("row")) {
                        tableLayout.addView(currentRow);
                    } else if (tag.equals("btn")) {
                        currentRow.addView(currentBtn);
                    }
                }

                xrp.next();
            }
        } catch (XmlPullParserException e) {
            // err
        } catch (IOException ioe) {
            // err
        }
    }

    private class KeypadClickListener implements View.OnClickListener {

        final String txt;

        private KeypadClickListener(String txt) {
            this.txt = txt;
        }

        @Override
        public void onClick(View v) {
            display.getText().append(txt);
        }
    }
}
