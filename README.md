API:
* 


Entities:

* User:
  Long id
  String name
  String email
  String password
  String avatarUrl

* Availability:
  User user
  LocalTime: unavailableFrom
  LocalTime: unavailableTo
  TemporaryAvailability availability

* Enum TemporaryAvailability
  AVAILABLE, UNAVAILABLE, TEMPORARY

//TODO date( game time planned)
* Lobby:
  Long id
  String title  
  LocalTime date
  User owner
  LobbyState state

* Enum LobbyState
      ACTIVE, PRESET


* Invite:
  Long id
  String message
  Lobby forLobby
  User user
  InviteState state

* Enum InviteState:
  PENDING, DECLINED, ACCEPTED, DELAYED

    
 