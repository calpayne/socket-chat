package com.calpayne.core.gui;

import com.calpayne.core.Nametag;
import com.calpayne.core.message.types.OnlineListDataMessage;
import com.google.gson.Gson;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Cal Payne
 */
public class OnlineList extends JLabel {

    private final ArrayList<Nametag> online;

    public OnlineList() {
        online = new ArrayList<>();

        this.setText("<html><style type=\"text/css\">p {margin-top: 2px;font-weight: 300;} "
                + ".server {color: #856404;} "
                + ".normal {color: #004085;} "
                + ".admin {color: #155724;}</style>"
                + "<b>Who's Online</b><br /><br /></html>");
        this.setVerticalAlignment(JLabel.TOP);
        this.setVerticalTextPosition(JLabel.TOP);
        this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
    }

    public ArrayList<Nametag> getOnlineList() {
        return online;
    }

    public void addClient(Nametag nametag) {
        online.add(nametag);
        updateList();
    }

    public void removeClient(String handle) {
        for (Nametag nt : online) {
            if (nt.getHandle().equalsIgnoreCase(handle)) {
                online.remove(nt);
                break;
            }
        }

        updateList();
    }

    public void updateList(OnlineListDataMessage oldm) {
        updateList(oldm.getOnline());
    }

    private void updateList() {
        updateList(online);
    }

    private void updateList(ArrayList<Nametag> list) {
        SwingUtilities.invokeLater(() -> {
            String textToSet = "<html><style type=\"text/css\">p {margin-top: 2px;font-weight: 300;} "
                    + ".server {color: #856404;} "
                    + ".normal {color: #004085;} "
                    + ".admin {color: #155724;}</style>"
                    + "<b>Who's Online</b><br /><br />";

            textToSet = online.stream().map((nt) -> nt.toString()).reduce(textToSet, String::concat);

            textToSet += "</html>";
            this.setText(textToSet);
        });
    }

    /**
     * @return JSON string of the online list
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
