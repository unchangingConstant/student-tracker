module io.github.unchangingconstant.studenttracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.xerial.sqlitejdbc;

    requires org.jdbi.v3.core;
    requires org.jdbi.v3.sqlobject;
    requires org.slf4j;

    // Only required in compile-time
    requires static lombok;

    // lombok and jdbi use reflection for their functions. So this will be required
    opens io.github.unchangingconstant.studenttracker.domainentities to org.jdbi.v3.core;
    // Same deal, fxml uses reflection and so will need special treatment
    opens io.github.unchangingconstant.studenttracker.viewmodels.impl to javafx.fxml;

    exports io.github.unchangingconstant.studenttracker;
}
