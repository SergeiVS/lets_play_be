Entities:
* User:
    Long id
    String name
    String email
    String password

* Availability:

    
* Lobby:
  Long id
  String title  
  LocalTime date
  User owner
  Set<User> invitedUsers
        
* Invite:
    Long id
    String message
    InviteState state
    Lobby forLobby
    

* Enum InviteState:
        PENDING, DECLINED, ACCEPTED, DELAYED
* 
    
 