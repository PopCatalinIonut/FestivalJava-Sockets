package model.validator;

import model.Ticket;

public class TicketValidator  implements  Validator<Ticket>{
    @Override
    public void validate(Ticket entity) throws ValidationException {

        int errors=0;

        if(entity.getID()==null || entity.getBuyerName()==null )
            errors++;

        if(entity.getSeats()<0)
            errors++;

        if(errors>0)
            throw new ValidationException();
    }
}
