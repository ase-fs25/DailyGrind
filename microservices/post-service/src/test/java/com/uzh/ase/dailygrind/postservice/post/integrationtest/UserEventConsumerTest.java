package com.uzh.ase.dailygrind.postservice.post.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.postservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.postservice.post.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.UserRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@Import({LocalStackTestConfig.class, DynamoDBConfig.class})
public class UserEventConsumerTest {

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${dg.us.aws.sqs.queue-url}")
    private String queueUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<FriendEntity> friendTable;

    @Autowired
    private DynamoDbTable<PostEntity> postTable;

    @Autowired
    private DynamoDbTable<CommentEntity> commentTable;

    @AfterEach
    void tearDown() {
        userTable.scan().items().forEach(userTable::deleteItem);
        friendTable.scan().items().forEach(friendTable::deleteItem);
        postTable.scan().items().forEach(postTable::deleteItem);
        commentTable.scan().items().forEach(commentTable::deleteItem);


        // Drain remaining messages in the queue
        while (true) {
            ReceiveMessageResponse response = sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(1)
                    .build()
            );
            if (response.messages().isEmpty()) {
                break;
            }

            for (Message msg : response.messages()) {
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build());
            }
        }
    }

    @Test
    void testUserCreatedEventHandledFromSqsQueue() throws Exception {
        // Given
        UserDataEvent event = new UserDataEvent(
            "user-123",
            "alice@example.com",
            "Alice",
            "Im Wundererland",
            "url"
        );

        String body = objectMapper.writeValueAsString(event);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(body)
            .messageAttributes(Map.of(
                "eventType", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("USER_CREATED")
                    .build()
            ))
            .build();

        // When
        sqsClient.sendMessage(sendRequest);

        // Then
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(250))
            .untilAsserted(() -> {
                var user = userRepository.getUser("user-123");
                assertThat(user)
                    .isNotNull()
                    .satisfies(u -> {
                        assertThat(u.getUserId()).isEqualTo("user-123");
                        assertThat(u.getEmail()).isEqualTo("alice@example.com");
                        assertThat(u.getFirstName()).isEqualTo("Alice");
                        assertThat(u.getLastName()).isEqualTo("Im Wundererland");
                        assertThat(u.getProfilePictureUrl()).isEqualTo("url");
                    });
            });
    }

    @Test
    void testUserUpdatedEventHandledFromSqsQueue() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("user-123"))
            .sk(UserEntity.generateSK())
            .email("alice@example.com")
            .firstName("Alice")
            .lastName("Alter Name")
            .build();
        userRepository.addNewUser(userEntity);

        UserDataEvent event = new UserDataEvent(
            "user-123",
            "alice@example.com",
            "Alice",
            "Im Wundererland",
            "url"
        );

        String body = objectMapper.writeValueAsString(event);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(body)
            .messageAttributes(Map.of(
                "eventType", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("USER_UPDATED")
                    .build()
            ))
            .build();

        // When
        sqsClient.sendMessage(sendRequest);

        // Then
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(250))
            .untilAsserted(() -> {
                var user = userRepository.getUser("user-123");
                assertThat(user)
                    .isNotNull()
                    .satisfies(u -> {
                        assertThat(u.getUserId()).isEqualTo("user-123");
                        assertThat(u.getEmail()).isEqualTo("alice@example.com");
                        assertThat(u.getFirstName()).isEqualTo("Alice");
                        assertThat(u.getLastName()).isEqualTo("Im Wundererland");
                        assertThat(u.getProfilePictureUrl()).isEqualTo("url");
                    });
            });
    }

    @Test
    void testUserDeletedEventHandledFromSqsQueue() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("user-123"))
            .sk(UserEntity.generateSK())
            .email("alice@example.com")
            .firstName("Alice")
            .lastName("Alter Name")
            .build();
        userRepository.addNewUser(userEntity);

        UserDataEvent event = new UserDataEvent(
            "user-123",
            "alice@example.com",
            "Alice",
            "Im Wundererland",
            "url"
        );

        String body = objectMapper.writeValueAsString(event);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(body)
            .messageAttributes(Map.of(
                "eventType", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("USER_DELETED")
                    .build()
            ))
            .build();

        // When
        sqsClient.sendMessage(sendRequest);

        // Then
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(250))
            .untilAsserted(() ->
                assertThat(userRepository.getUser("user-123"))
                    .isNull()
            );
    }

    @Test
    void testFriendshipCreatedEventHandledFromSqsQueue() throws Exception {
        // Given: two users already exist
        UserEntity user1 = UserEntity.builder()
            .pk(UserEntity.generatePK("user-123"))
            .sk(UserEntity.generateSK())
            .email("user1@example.com")
            .firstName("User")
            .lastName("One")
            .build();

        UserEntity user2 = UserEntity.builder()
            .pk(UserEntity.generatePK("user-456"))
            .sk(UserEntity.generateSK())
            .email("user2@example.com")
            .firstName("User")
            .lastName("Two")
            .build();

        userRepository.addNewUser(user1);
        userRepository.addNewUser(user2);

        FriendshipEvent friendshipEvent = new FriendshipEvent(
            "user-123",
            "user-456"
        );

        String body = objectMapper.writeValueAsString(friendshipEvent);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(body)
            .messageAttributes(Map.of(
                "eventType", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("FRIENDSHIP_CREATED")
                    .build()
            ))
            .build();

        // When
        sqsClient.sendMessage(sendRequest);

        // Then (assumes bidirectional friendship or at least one-directional recorded)
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(250))
            .untilAsserted(() -> {
                var updatedUser = userRepository.getUser("user-123");
                assertThat(updatedUser)
                    .isNotNull()
                    .satisfies(u -> assertThat(userRepository.getFriendIds(u.getUserId())).contains("user-456"));
            });
    }

    @Test
    void testFriendshipDeletedEventHandledFromSqsQueue() throws Exception {
        // Given: user with an existing friend
        UserEntity user1 = UserEntity.builder()
            .pk(UserEntity.generatePK("user-123"))
            .sk(UserEntity.generateSK())
            .email("user1@example.com")
            .firstName("User")
            .lastName("One")
            .build();
        userRepository.addNewUser(user1);

        UserEntity user2 = UserEntity.builder()
            .pk(UserEntity.generatePK("user-456"))
            .sk(UserEntity.generateSK())
            .email("user2@example.com")
            .firstName("User")
            .lastName("Two")
            .build();
        userRepository.addNewUser(user2);

        FriendshipEvent friendshipEvent = new FriendshipEvent(
            "user-123",
            "user-456"
        );

        String body = objectMapper.writeValueAsString(friendshipEvent);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(body)
            .messageAttributes(Map.of(
                "eventType", MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue("FRIENDSHIP_DELETED")
                    .build()
            ))
            .build();

        // When
        sqsClient.sendMessage(sendRequest);

        // Then
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(250))
            .untilAsserted(() -> {
                var updatedUser = userRepository.getUser("user-123");
                assertThat(updatedUser)
                    .isNotNull()
                    .satisfies(u -> assertThat(userRepository.getFriendIds(u.getUserId())).doesNotContain("user-456"));
            });
    }

    @Test
    void testUserDeletedAlsoRemovesPostsCommentsAndFriends() throws Exception {
        // Given: a user with posts and comments
        String userId = "user-123";

        // Save user
        UserEntity user1 = UserEntity.builder()
                .pk(UserEntity.generatePK(userId))
                .sk(UserEntity.generateSK())
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Im Wundererland")
                .build();
        userRepository.addNewUser(user1);

        UserEntity user2 = UserEntity.builder()
                .pk(UserEntity.generatePK("other-user"))
                .sk(UserEntity.generateSK())
                .email("test")
                .firstName("Test")
                .lastName("Test")
                .build();
        userRepository.addNewUser(user2);

        // Save friend
        FriendEntity friendEntity = FriendEntity.builder()
                .pk(FriendEntity.generatePK(userId))
                .sk(FriendEntity.generateSK("other-user"))
                .build();
        friendTable.putItem(friendEntity);

        FriendEntity friendEntity2 = FriendEntity.builder()
                .pk(FriendEntity.generatePK("other-user"))
                .sk(FriendEntity.generateSK(userId))
                .build();
        friendTable.putItem(friendEntity2);

        // Save post
        PostEntity post1 = PostEntity.builder()
                .pk(PostEntity.generatePK(userId))
                .sk(PostEntity.generateSK("post-1"))
                .postContent("My first post")
                .build();
        postRepository.savePost(post1);

        PostEntity post2 = PostEntity.builder()
                .pk(PostEntity.generatePK("other-user"))
                .sk(PostEntity.generateSK("post-2"))
                .postContent("My second post")
                .build();
        postRepository.savePost(post2);

        // Save comment
        CommentEntity comment1 = CommentEntity.builder()
                .pk(CommentEntity.generatePK(userId, "post-1"))
                .sk(CommentEntity.generateSK("comment-1"))
                .commentContent("My comment")
                .build();
        commentRepository.saveComment(comment1);

        CommentEntity comment2 = CommentEntity.builder()
                .pk(CommentEntity.generatePK(userId, "post-2"))
                .sk(CommentEntity.generateSK("comment-2"))
                .commentContent("My comment")
                .build();
        commentRepository.saveComment(comment2);

        // Send USER_DELETED event
        UserDataEvent deleteEvent = new UserDataEvent(
                userId,
                "alice@example.com",
                "Alice",
                "Im Wundererland",
                "url"
        );

        String body = objectMapper.writeValueAsString(deleteEvent);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .messageAttributes(Map.of(
                        "eventType", MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue("USER_DELETED")
                                .build()
                ))
                .build();

        // When
        sqsClient.sendMessage(sendRequest);

        // Then
        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> {
                    assertThat(userRepository.getUser(userId)).isNull();
                    assertThat(postRepository.findPostById("post-1")).isNull();
                    assertThat(commentRepository.findCommentById("comment-1")).isNull();
                    assertThat(commentRepository.findCommentById("comment-2")).isNull();
                    assertThat(commentRepository.findAllCommentsForPost("post-2")).isEmpty();
                    assertThat(postRepository.findPostById("post-2")).isNotNull();
                    assertThat(userRepository.getUser("other-user")).isNotNull();
                    assertThat(userRepository.getFriendIds("other-user")).doesNotContain(userId);
                });
    }


}
