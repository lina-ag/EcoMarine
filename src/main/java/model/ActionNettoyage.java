package model;

public class ActionNettoyage {
    private int idAction;
    private String dateAction;
    private String lieu;

    public ActionNettoyage() {
    }

    public ActionNettoyage(int idAction, String dateAction, String lieu) {
        this.idAction = idAction;
        this.dateAction = dateAction;
        this.lieu = lieu;
    }

    public int getIdAction() {
        return idAction;
    }

    public void setIdAction(int idAction) {
        this.idAction = idAction;
    }

    public String getDateAction() {
        return dateAction;
    }

    public void setDateAction(String dateAction) {
        this.dateAction = dateAction;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
}