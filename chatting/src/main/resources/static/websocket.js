let websocket;

function onOpen() {
  let username = document.getElementById("username");
  websocket.send(username.value + ":" +"connected");
  console.log("connected: onOpen()");
}

function onMessage(receivedMessage) {
  showMessage(receivedMessage.data);
  console.log("received: onMessage()");
}

function onClose() {
  console.log("disconnected: onClose()");
}

function connect() {
  websocket = new WebSocket("ws://localhost:8080/ws/chats");
  websocket.onmessage = onMessage;
  websocket.onopen = onOpen;
  websocket.onclose = onClose;

  setConnected(true);
  console.log("connected: connect()");
}

function disconnect() {
  websocket.close(); //웹소켓 닫아주기

  setConnected(false);
  console.log("disconnected: disconnect()");
}

function sendMessage() {
  let username = document.getElementById("username");
  let message = document.getElementById("message");

  websocket.send(username.value + ":" + message.value); //메시지를 보내기
  message.value = "";
  console.log("sent: send()");
}

function showMessage(message) {
  $("#messages").append("<tr><td>" + message + "</td></tr>"); //서버로 부터 받는 메시지를 추가
}

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#messages").html("");
}

$(function () {
  $("#connect").click(() => connect());//각 버튼클릭 했을떄 실행
  $("#disconnect").click(() => disconnect());
  $("#send").click(() => sendMessage());
});



//기본 WebSocket API를 사용하는 자바스크립트 코드입니다.
//  함수	설명
//  connect()	ws://localhost:8080/ws/chats에 WebSocket 연결. 연결되면 이벤트 핸들러 등록 (onOpen, onMessage, onClose).
//  disconnect()	연결 종료하고 버튼 상태 변경.
//  sendMessage()	입력한 사용자명과 메시지를 :으로 묶어 서버에 전송.
//  onOpen()	연결 직후 실행. 사용자 이름을 서버에 알림.
//  onMessage()	서버에서 메시지를 받을 때 실행. HTML에 메시지 추가.
//  onClose()	연결이 끊어졌을 때 실행.
//  setConnected()	연결 상태에 따라 UI 버튼과 메시지 영역 제어.
//  showMessage()	메시지를 HTML 테이블에 <tr>로 추가.