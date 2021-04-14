package model.validator;

import model.Show;

public class ShowValidator implements  Validator<Show>{
    @Override
    public void validate(Show entity) throws ValidationException {

        int errors=0;
        if(entity.getID()==null || entity.getDate()==null || entity.getArtist()==null || entity.getDate()==null || entity.getLocation()==null)
            errors++;

        if(entity.getTotalSeats()<0 || entity.getSoldSeats()>entity.getTotalSeats())
            errors++;

        if(errors>0)
            throw new ValidationException();
    }
}
