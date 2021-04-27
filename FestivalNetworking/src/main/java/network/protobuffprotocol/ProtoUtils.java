package network.protobuffprotocol;

import model.Artist;
import model.Show;
import model.Ticket;
import model.User;

import java.time.LocalDateTime;
import java.util.Arrays;

public class ProtoUtils {
    public static FestivalProtobufs.FestivalRequest createLoginRequest(User user){
        FestivalProtobufs.User protoUser=FestivalProtobufs.User.newBuilder().setId(user.getId()).setPasswd(user.getPasswd()).build();
        FestivalProtobufs.FestivalRequest request= FestivalProtobufs.FestivalRequest.newBuilder().setType(FestivalProtobufs.FestivalRequest.Type.LOGIN)
                .setUser(protoUser).build();
        return request;
    }
    public static FestivalProtobufs.FestivalRequest createLogoutRequest(User user){
        FestivalProtobufs.User userDTO=FestivalProtobufs.User.newBuilder().setId(user.getId()).build();
        FestivalProtobufs.FestivalRequest request= FestivalProtobufs.FestivalRequest.newBuilder().setType(FestivalProtobufs.FestivalRequest.Type.LOGOUT)
                .setUser(userDTO).build();
        return request;
    }

    public static FestivalProtobufs.FestivalRequest createGetAllShowsRequest(){
        FestivalProtobufs.FestivalRequest request = FestivalProtobufs.FestivalRequest.newBuilder().setType(FestivalProtobufs.FestivalRequest.Type.GET_ALL_SHOWS).build();
        return request;
    }
    public static FestivalProtobufs.FestivalRequest createGetShowsByDateRequest(LocalDateTime date){
        FestivalProtobufs.Date protoDate = getProtoDateFromDate(date);
        FestivalProtobufs.FestivalRequest request = FestivalProtobufs.FestivalRequest.newBuilder().setDate(protoDate).setType(FestivalProtobufs.FestivalRequest.Type.GET_SHOWS_BY_DATE).build();
        return request;
    }

    public static FestivalProtobufs.FestivalRequest createBuyTicketsRequest(Ticket ticket){
        FestivalProtobufs.FestivalRequest request = FestivalProtobufs.FestivalRequest.newBuilder().setType(FestivalProtobufs.FestivalRequest.Type.BUY_TICKETS).setTicket(getProtoTicket(ticket)).build();
        return request;
    }
    public static FestivalProtobufs.FestivalResponse createGetShowsResponse(Show[] shows){
        Iterable<Show> showList = Arrays.asList(shows);
        FestivalProtobufs.FestivalResponse.Builder response= FestivalProtobufs.FestivalResponse.newBuilder().setType(FestivalProtobufs.FestivalResponse.Type.GET_SHOWS);
        for(Show show: shows)
        response.addShows(getProtoShowfromShow(show));
        return  response.build();
    }

    public static FestivalProtobufs.FestivalResponse createOkResponse(){
        FestivalProtobufs.FestivalResponse response=FestivalProtobufs.FestivalResponse.newBuilder()
                .setType(FestivalProtobufs.FestivalResponse.Type.OK).build();
        return response;
    }

    public static FestivalProtobufs.FestivalResponse createErrorResponse(String text){
        FestivalProtobufs.FestivalResponse response=FestivalProtobufs.FestivalResponse.newBuilder()
                .setType(FestivalProtobufs.FestivalResponse.Type.ERROR)
                .setError(text).build();
        return response;
    }

    public static String getError(FestivalProtobufs.FestivalResponse response){
        String errorMessage=response.getError();
        return errorMessage;
    }

    public static User getUser(FestivalProtobufs.FestivalRequest request){
        User user=new User();
        user.setId(request.getUser().getId());
        user.setPasswd(request.getUser().getPasswd());
        return user;
    }

    public static Artist getArtistfromProtoArtist(FestivalProtobufs.Artist artist){
        Artist newartist = new Artist();
        newartist.setID(artist.getID());
        newartist.setName(artist.getName());
        return newartist;
    }

    public static FestivalProtobufs.Artist getProtoArtistfromArtist(Artist artist){
        return FestivalProtobufs.Artist.newBuilder().setID(artist.getID()).setName(artist.getName()).build();
    }

    public static FestivalProtobufs.Show getProtoShowfromShow(Show show){
        return FestivalProtobufs.Show.newBuilder().setArtist(getProtoArtistfromArtist(show.getArtist())).setDate(getProtoDateFromDate(show.getDate())).
                setID(show.getID()).setLocation(show.getLocation()).setName(show.getName()).setSoldSeats(show.getSoldSeats())
                .setTotalSeats(show.getTotalSeats()).build();
    }
    public static LocalDateTime getDatefromProtoDate(FestivalProtobufs.Date date){
        LocalDateTime newDate = LocalDateTime.of(date.getYear(),
                date.getMonth(), date.getDay(), date.getHour(), date.getMinute(), date.getSecond());
        return newDate;
    }

    public static FestivalProtobufs.Date getProtoDateFromDate(LocalDateTime date){
        return  FestivalProtobufs.Date.newBuilder().setDay(date.getDayOfMonth()).setMonth(date.getMonthValue()).setYear(date.getYear())
                .setHour(date.getHour()).setMinute(date.getMinute()).setSecond(date.getSecond()).build();
    }
    public static Show[] getShows(FestivalProtobufs.FestivalResponse response){
        Show[] allShows=new Show[response.getShowsCount()];
        for(int i=0;i<response.getShowsCount();i++){
            FestivalProtobufs.Show protoShow=response.getShows(i);
            Artist artist = getArtistfromProtoArtist(protoShow.getArtist());
            LocalDateTime date = getDatefromProtoDate(protoShow.getDate());
            Show show = new Show(protoShow.getID(), protoShow.getName(), protoShow.getLocation(), date,artist,protoShow.getTotalSeats(),protoShow.getSoldSeats());
            allShows[i]=show;
        }
        return allShows;
    }

    public static FestivalProtobufs.Ticket getProtoTicket(Ticket ticket){
        return FestivalProtobufs.Ticket.newBuilder().setBuyerName(ticket.getBuyerName()).setFestivalID(ticket.getFestivalID()).setID(ticket.getID()).setSeats(ticket.getSeats()).build();
    }
}
