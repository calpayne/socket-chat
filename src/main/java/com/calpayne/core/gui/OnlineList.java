package com.calpayne.core.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 *
 * @author Cal Payne
 */
public class OnlineList extends JLabel {

    private final ArrayList<String> online;

    public OnlineList() {
        online = new ArrayList<>();

        this.setText("<html><style type=\"text/css\">p {margin-top: 2px;font-weight: 300;} p b {color: #856404;}</style><b>Who's Online</b><br /><br /></html>");
        this.setVerticalAlignment(JLabel.TOP);
        this.setVerticalTextPosition(JLabel.TOP);
        this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
    }

    public ArrayList<String> getOnlineList() {
        return online;
    }

    public void addClient(String handle) {
        online.add(handle);
        updateList();
    }

    public void removeClient(String handle) {
        online.remove(handle);
        updateList();
    }

    public void updateListFromJSON(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        ArrayList<String> newOnline = gson.fromJson(json, ArrayList.class);

        updateList(newOnline);
    }

    private void updateList() {
        updateList(online);
    }

    private void updateList(ArrayList<String> list) {
        String textToSet = "<html><style type=\"text/css\">p {margin-top: 2px;font-weight: 300;} p b {color: #856404;}</style><b>Who's Online</b><br /><br />";
        textToSet = list.stream().map((client) -> "<p>" + client + "</p>").reduce(textToSet, String::concat);
        textToSet += "</html>";

        this.setText(textToSet);
    }

    /**
     * @return JSON string of the online list
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
