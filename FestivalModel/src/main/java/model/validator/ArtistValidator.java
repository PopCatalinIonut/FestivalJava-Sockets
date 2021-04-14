package model.validator;

import model.Artist;

public class ArtistValidator implements Validator<Artist> {

    @Override
    public void validate(Artist entity) throws ValidationException {

        int errors=0;
        if(entity.getID()==null || entity.getName()==null)
            errors++;

        if(errors>0)
            throw new ValidationException();
    }
}
