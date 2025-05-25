const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats'
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  showChatrooms(0);
  stompClient.subscribe('/sub/chats/updates',  //클라이언트가 이를 커넥버튼 눌르면 구독하고 있다가
      (chatMessage) => {
        toggleNewMessageIcon(JSON.parse(chatMessage.body).id, true); //메시지를 수신하면 toggleNewMessageIcon() 콜백 함수를 실행
        updateMemberCount(JSON.parse(chatMessage.body));
ㅣㄷ
      });
  console.log('Connected: ' + frame);
};

//현재 내가 입장해 있는 채팅방 ID (#chatroom-id input의 값)와
// 이 함수에 전달된 chatroomId가 같다면
//👉 지금 내가 보고 있는 방이므로 굳이 새 메시지 아이콘을 표시할 필요가 없음 → return으로 종료
function toggleNewMessageIcon(chatroomId, toggle) {
  if(chatroomId == $("#chatroom-id").val()){
    return;
  }
  if (toggle) {
    $("#new_" + chatroomId).show();
  } else {
    $("#new_" + chatroomId).hide();
  }
}

function updateMemberCount(chatroom){
  $("#memberCount_" + chatroom.id).html(chatroom.memberCount)
}


stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#create").prop("disabled", !connected);
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendMessage() {
  let chatroomId = $("#chatroom-id").val();
  stompClient.publish({
    destination: "/pub/chats/" + chatroomId,
    body: JSON.stringify(
        {'message': $("#message").val()})
  });
  $("#message").val("")
}

function createChatroom() {
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats?title=' + $("#chatroom-title").val(),
    success: function (data) {
      console.log('data: ', data);
      showChatrooms(0);
      enterChatroom(data.id, true);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function showChatrooms(pageNumber) { //서버로 부터 가지고온 페이지 넘버
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/consultants/chats?sorts=id,desc&page='+pageNumber,
    success: function (data) {
      console.log('data: ', data);
      renderChatrooms(data);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function renderChatrooms(page) {
  let chatrooms = page.content
  $("#chatroom-list").html("");
  for (let i = 0; i < chatrooms.length; i++) {
    $("#chatroom-list").append(
        "<tr onclick='joinChatroom(" + chatrooms[i].id + ")'><td>"
        + chatrooms[i].id + "</td><td>" + chatrooms[i].title
        + "<img src='new.png' id='new_" + chatrooms[i].id + "' style='display: "
        + getDisplayValue(chatrooms[i].hasNewMessage) + "'/></td><td id='memberCount_" + chatrooms[i].id +"'>"
        + chatrooms[i].memberCount + "</td><td>" + chatrooms[i].createdAt
        + "</td></tr>"
    );
  }
  if(page.first){ // 첫번째 페이지면 이전버튼 비활성화 시키고 아니면 활성화 시켜 showChatrooms 로 요청할때 그전 페이지 번호를 파라미터로 보냄
    $("#prev").prop("disabled",true);
  }else{
    $("#prev").prop("disabled",false).click(()=>showChatrooms(page.number-1));
  }
  if(page.last){
    $("#next").prop("disabled",true);
  }
  else{
    $("#next").prop("disabled",false).click(()=>showChatrooms(page.number+1));
  }
}

function getDisplayValue(hasNewMessage) {
  if (hasNewMessage) {
    return "inline";
  }

  return "none";
}

let subscription;

function enterChatroom(chatroomId, newMember) {
  $("#chatroom-id").val(chatroomId);
  $("#messages").html("");
  showMessages(chatroomId);
  $("#conversation").show();
  $("#send").prop("disabled", false);
  $("#leave").prop("disabled", false);
  toggleNewMessageIcon(chatroomId, false);

  if (subscription != undefined) {
    subscription.unsubscribe();
  }

  subscription = stompClient.subscribe('/sub/chats/' + chatroomId,
      (chatMessage) => {
        showMessage(JSON.parse(chatMessage.body));
      });

  if (newMember) {
    stompClient.publish({
      destination: "/pub/chats/" + chatroomId,
      body: JSON.stringify(
          {'message': "님이 방에 들어왔습니다."})
    })
  }
}

function showMessages(chatroomId) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats/' + chatroomId + '/messages',
    success: function (data) {
      console.log('data: ', data);
      for (let i = 0; i < data.length; i++) {
        showMessage(data[i]);
      }
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function showMessage(chatMessage) {
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>");
}

function joinChatroom(chatroomId) {
  let currentChatroomId = $("#chatroom-id").val(); //기존에 값을 currentChatroomId 로 넣어줘서

  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats/' + chatroomId + getRequestParam(currentChatroomId),
    success: function (data) {
      console.log('data: ', data);
      enterChatroom(chatroomId, data);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function getRequestParam(currentChatroomId) {
  if (currentChatroomId == "") {
    return "";
  }

  return "?currentChatroomId=" + currentChatroomId;
}

function leaveChatroom() {
  let chatroomId = $("#chatroom-id").val();
  $.ajax({
    type: 'DELETE',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      showChatrooms(0);
      exitChatroom(chatroomId);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function exitChatroom(chatroomId) {
  $("#chatroom-id").val("");
  $("#conversation").hide();
  $("#send").prop("disabled", true);
  $("#leave").prop("disabled", true);
}

$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#create").click(() => createChatroom());
  $("#leave").click(() => leaveChatroom());
  $("#send").click(() => sendMessage());
});