The Application is created for coordination of the online gaming community. 
Every user can register him/herself into the community. 
The registered User has name, login credentials avatar and reference to his/her Availability.

Availability has reference to User,
availability state (AVAILABLE, UNAVAILABLE, TEMPORARY_UNAVAILABLE)
and in case of temporary unavailability unavailableFrom and unavailableTo fields.

Registered User has the possibility to create his/her own playroom(lobby).
The User who had created the Lobby is its owner.
To create the lobby needs to be set title, time of the game and preferred Users or without them.
The Lobby has two states:
- PRESET (no Invite sent to saved gamers or evan no Users saved by owner).
- ACTIVE (The Invites are sent to Users saved).

In a case of inviting User will receive the Invite with a message and information about Lobby into were invited and
Users were invited.
User can accept, decline or delay invite.
All invited Users will know how many gamers are accepted or declined the Invite.



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
  AVAILABLE, UNAVAILABLE, TEMPORARY_UNAVAILABLE

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
  Lobby lobby
  User user
  InviteState state

* Enum InviteState:
  PENDING, DECLINED, ACCEPTED, DELAYED

    
 