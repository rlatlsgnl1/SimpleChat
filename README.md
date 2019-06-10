# SimpleChat
Simple Chatting Program (java)

# HOW TO USE?
# 아래의 내용을 수정해주세요.
## Client Compile 방법
javac ChatClient.java
java ChatClient


## Server Compile 방법
javac ChatServer.java
java ChatServer
## Test 방법
### Terminal #1
java ChatServer
### Terminal #2
java ChatClient 만 입력하면 실행 후 name과 ip입력 가능
### Terminal #3
java ChatClient 만 입력하면 실행 후 name과 ip입력 가능

## Lab5: Customizing 1
- 1. ChatClient 실행 구문 변경하기
username과 server ip를 program을 실행할 때 입력하지 않고, 실행하고 난 후 입력할 수 있도록 함.
- 2. broadcast(), sendmsg()에서 클라이언트에게 보내는 메시지 앞부분에 현재시간을 보여주는 기능 추가
ChatServer에서 SimpleDateFormat을 사용해
SimpleDateFormat f1=new SimpleDateFormat("[a hh:mm]");
pw를 통해 Client에게 메시지와 함께 전달되도록 함.

## Lab6: Customizing 2
- 1. 현재 접속한 사용자 목록 보기 기능
/userlist 입력시 사용자 목록이 보임
하지만 아이디 전체가 보이지 않고 앞 세 자리만 보이도록 함.

- 2. 자신이 보낸 채팅 문장은 자신에게는 나타나지 않도록 할 것
ChatServer에서 HashMap에 현재 아이디와 같은 key가 있으면 제외하도록 하여 자신에게 메시지가 가지 않도록 함
- 3. 금지어 경고 기능
ArrayList<String>을 생성해 금지어가 그 중에 있다면  warn( );을 call하여 경고할 수 있도록 함

## Lab7: Customizing 3
- 1. 클라이언트에서 '/spamlist' 를 입력하면 현재 서버에 등록된 금지어의 목록 출력 기능 구현 (미리 금지어가 등록되어 있을 필요 없음)
서버가 시작할 때 spamlist.txt를 불러와 그 안에 있는 단어들을 빈 ArrayList에 넣음
Client가 /spamlist를 입력하면 ArrayList size만큼 for문을 돌려 사용자에게 print함.
- 2. 클라이언트에서 '/addspam 단어'를 입력하면 해당 <단어>가 서버에 금지어로 추가되도록 하는 기능 구현
/addspam (단어)를 입력하면 ArrayList에 들어가는 동시에 그 리스트를 spamlist.txt에 덮어쓰도록 함.
- 3. 금지어 파일 관리 기능 구현 - 서버를 시작하면 금지어 리스트는 특정 파일에서 불러오고, 서버가 종료되면 새로 추가된 금지어를 포함한 현재 리스트가 파일에 저장되도록 기능 구현

++ 아이디가 3자리 이하이면 다시 입력하도록 설정
++ /userlist 를 입력하면 id를 앞 세자리만 보여주고 나머지는 *로 표시하도록 함.

> Q&A: 21400564@handong.edu
