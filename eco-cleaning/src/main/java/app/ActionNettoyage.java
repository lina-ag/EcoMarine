package app;

public class ActionNettoyage {
    private final int idAction;
    private final String dateAction;
    private final String lieu;

    public ActionNettoyage(int idAction, String dateAction, String lieu) {
        this.idAction = idAction;
        this.dateAction = dateAction;
        this.lieu = lieu;
    }
    @Override
    public String toString() {
        return idAction + " | " + dateAction + " | " + lieu;
    }

    public int getIdAction() { return idAction; }
    public String getDateAction() { return dateAction; }
    public String getLieu() { return lieu; }
}