package models.impl;

import java.util.ArrayList;
import java.util.List;

import models.Model;
import viewmodels.ViewModel;

public class StudentDatabaseModel implements Model {

    private List<ViewModel> viewmodels;

    public StudentDatabaseModel() {
        viewmodels = new ArrayList<ViewModel>();
    }

    @Override
    public void addViewModel(ViewModel viewmodel) {
        viewmodels.add(viewmodel);
    }

}
