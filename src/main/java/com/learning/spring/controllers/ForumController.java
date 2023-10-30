package com.learning.spring.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
// import java.text.ParseException;
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.spring.social.bindings.AddCommentForm;
import com.learning.spring.social.bindings.AddPostForm;
import com.learning.spring.social.bindings.RegistrationForm;
// import com.learning.spring.social.dto.PostDTO;
import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.entities.FavoriteAuthor;
// import com.learning.spring.social.entities.Favpost;
import com.learning.spring.social.entities.Like;
import com.learning.spring.social.entities.LikeId;
import com.learning.spring.social.entities.MutedAuthor;
// import com.learning.spring.social.entities.Mutedpost;
import com.learning.spring.social.entities.Notification;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.Tag;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.exceptions.ResourceNotFoundException;
// import com.learning.spring.social.repositories.FavPostRepository;
import com.learning.spring.social.repositories.FavoriteAuthorRepository;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.MutedAuthorRepository;
import com.learning.spring.social.repositories.MutedPostRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.TagRepository;
import com.learning.spring.social.repositories.UserRepository;
import com.learning.spring.social.service.CommentService;
import com.learning.spring.social.service.DomainUserService;
import com.learning.spring.social.service.FavMutePostService;
import com.learning.spring.social.service.NotificationService;
import com.learning.spring.social.service.PostService;
// import com.learning.spring.social.service.SortingPosts;
import com.learning.spring.social.service.SortingPosts;
import com.learning.spring.social.service.TaskDefinitionBean;
import com.learning.spring.social.service.TaskSchedulingService;
import com.learning.spring.social.utils.CronUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/forum")
public class ForumController {
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeCRUDRepository likeCRUDRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MutedPostRepository mutedPostRepository;

    // @Autowired
    // private FavPostRepository favPostRepository;

    @Autowired
    private FavMutePostService favMutePostService;

    @Autowired
    private TaskSchedulingService taskSchedulingService;


    @Autowired
    private CronUtil cronUtil;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @GetMapping
    public String home(Principal principal, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("isLoggedIn", principal != null);

        Optional<User> user = userRepository.findByName(userDetails.getUsername());
        List<Post> primposts = mutedPostRepository.findAllPostsNotMutedByUser(user.get());
        // List<PostDTO> posts = postService.createPostDTO(primposts);

        if (principal != null) {
            model.addAttribute("username", principal.getName());
            model.addAttribute("symbol", principal.getName().substring(0, 1));
        } else {
            model.addAttribute("username", "anonymous");
            model.addAttribute("symbol", "a");
        }
        model.addAttribute("posts", postService.findAll());
        return "forum/home";
    }

    @GetMapping("/tag/{name}")
    public String getPostsByTag(@PathVariable String name, Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            model.addAttribute("symbol", principal.getName().substring(0, 1));
        }
        model.addAttribute("posts", postService.findByPattern("#" + name));
        return "forum/home";
    }

    @GetMapping("/post/form")
    public String getPostForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("postForm", new AddPostForm());
        return "forum/postForm";
    }

    @GetMapping("/search")
    public String searchPost(@RequestParam("search") String search, Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            model.addAttribute("symbol", principal.getName().substring(0, 1));
        }
        if (search == null || search.isEmpty()) {
            return "redirect:/forum";
        } else {
            model.addAttribute("posts", postService.findByPattern(search));
        }
        return "forum/home";
    }

    @PostMapping("/post/add")
    @Transactional
    public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
            RedirectAttributes attr, @AuthenticationPrincipal UserDetails userDetails)
            throws ServletException, ParseException {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getFieldErrors());
            attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);
            attr.addFlashAttribute("post", postForm);
            return "redirect:/forum/post/form";
        }
        Set<Tag> postTags = new HashSet<>();
        String[] tags = postForm.getTags().split(",");
        for (int i = 0; i < tags.length; i++) {
            Tag existingTag = tagRepository.findByName(tags[i]);
            if (existingTag == null) {
                Tag newTag = new Tag();
                newTag.setName(tags[i]);
                tagRepository.save(newTag);
                postTags.add(newTag);
            } else {
                postTags.add(existingTag);
            }
        }
        User user = domainUserService.getByName(userDetails.getUsername()).get();
        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postForm.getContent());
        post.setTitle(postForm.getTitle());
        post.setTags(postTags);
        // postRepository.save(post);
        if (postForm.getDateTime() != null) {
            String cronExpression = cronUtil.dateToCronExpression(postForm.getDateTime().toString());
            String jobId = "post_" + UUID.randomUUID().toString();
            TaskDefinitionBean taskBean = new TaskDefinitionBean(post, postRepository, user, postForm,
                    notificationService);
            taskSchedulingService.scheduleATask(jobId, taskBean, cronExpression);
        } else {
            postRepository.save(post);
            notificationService.createNotification(user, post, "POST",
                    "You added a post (" + postForm.getTitle() + ").");
        }

        return "redirect:/forum";
    }

    @PostMapping("/post/{id}/like")
    public String postLike(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes attr) {
        User user = domainUserService.getByName(userDetails.getUsername()).get();
        // notificationService.createNotification(user,postRepository.findById(id).get(),
        // "like", user.getName()+" Liked a post!");
        LikeId likeId = new LikeId();
        Post post = postRepository.findById(id).get();
        likeId.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        likeId.setPost(postRepository.findById(id).get());
        Like like = new Like();
        like.setLikeId(likeId);
        likeCRUDRepository.save(like);
        if (userRepository.findByName(userDetails.getUsername()).get().equals(post.getAuthor())) {
            notificationService.createNotification(postRepository.findById(id).get().getAuthor(),
                    postRepository.findById(id).get(), "LIKE",
                    "you, liked your post (" + postRepository.findById(id).get().getTitle() + ").");
        } else {
            notificationService.createNotification(postRepository.findById(id).get().getAuthor(),
                    postRepository.findById(id).get(), "LIKE", userDetails.getUsername() + " liked your post ("
                            + postRepository.findById(id).get().getTitle() + ").");
        }
        return "redirect:/forum";
    }

    @PostMapping("/post/{id}/comment")
    public String commentOnPost(@ModelAttribute("commentForm") AddCommentForm commentForm, @PathVariable int id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = domainUserService.getByName(userDetails.getUsername()).get();
        // notificationService.createNotification(user,postRepository.findById(id).get(),
        // "comment", user.getName()+" Commented on the post!");
        Post post = postRepository.findById(id).get();
        int postId = post.getId();
        Comment comment = new Comment();
        comment.setContent(commentForm.getContent());
        comment.setPost(post);
        comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        commentService.save(comment);
        if (userRepository.findByName(userDetails.getUsername()).get().equals(post.getAuthor())) {
            notificationService.createNotification(postRepository.findById(postId).get().getAuthor(), post, "COMMENT",
                    "You, commented on your post (" + post.getTitle() + ").");
        } else {
            notificationService.createNotification(postRepository.findById(postId).get().getAuthor(), post, "COMMENT",
                    userDetails.getUsername() + ", commented on your post (" + post.getTitle() + ").");
        }
        return "redirect:/forum";
    }

    @PostMapping("/post/{id}/reply/{parentId}")
    public String replyToComment(@RequestParam("content") String content, @PathVariable int id,
            @PathVariable int parentId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> post = postRepository.findById(id);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post.get());
        comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        comment.setParent(commentService.findById(parentId).get());
        commentService.save(comment);
        return "redirect:/forum";
    }

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "forum/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
            attr.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }
        if (!registrationForm.isValid()) {
            attr.addFlashAttribute("message", "Passwords must match");
            attr.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }
        // domainUserService.save(registrationForm.getUsername(), registrationForm.getPassword());
        domainUserService.save(registrationForm.getUsername(), registrationForm.getPassword(),

				registrationForm.getBio(), registrationForm.getDateOfBirth(), registrationForm.getEmail());

		attr.addFlashAttribute("result", "Registration success!");
        attr.addFlashAttribute("result", "Registration success!");
        return "redirect:/login";
    }

    @GetMapping("/notifications")
    public String notificationPage(Model model, @AuthenticationPrincipal UserDetails userDetails, Principal principal)
            throws ResourceNotFoundException {
        // List<Notification> notificationList = notificationRepository.findAll();
        model.addAttribute("isLoggedIn", principal != null);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        User user = userRepository.findByName(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        System.out.println("inside get of notification");
        List<Notification> notificationList = notificationService.getNotificationsForUser(user);
        System.out.println(notificationList.toString());
        model.addAttribute("notificationList", notificationList);
        return "forum/notification";
    }

    @PostMapping("/notification/{notificationId}")
    public String handleNotificationForm(@PathVariable("notificationId") int postId) {
        System.out.println("Received notification ID: " + postId);
        System.out.println("----------------------------------------");

        return String.format("redirect:/forum/post/%d", postId);
    }

    // adding post to fav
    @PostMapping("/post/{id}/fav")
    public String addFavoritePost(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        String result = favMutePostService.addFavoritePost(id, userDetails);

        if (result.equals("redirect:/forum")) {
            return "redirect:/forum";
        } else {
            redirectAttributes.addFlashAttribute("FavMessage", result);
            return "redirect:/forum";
        }
    }

    // show the favourite post feed
    @GetMapping("/post/favfeed")
    public String favpostfeed(Model model, @AuthenticationPrincipal UserDetails userDetails)
            throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByName(userDetails.getUsername());
        // favpostList = favPostRepository.findAllByUser(user.get());
        favMutePostService.favpostList = favMutePostService.findAllFavPostsByUser(user.get());
        model.addAttribute("favpostList", favMutePostService.favpostList);
        model.addAttribute("commenterName", userDetails.getUsername());
        return "forum/favPost";
    }

    // delete a post from favourite feed
    @PostMapping("/post/favfeed/{postId}/delete")
    public String deleteFavPost(@PathVariable int postId, String commenterName) {
        Optional<User> user = userRepository.findByName(commenterName);
        Optional<Post> post = postRepository.findById(postId);
        if (user.isPresent() && post.isPresent()) {
            favMutePostService.deleteFavPost(user.get(), post.get());
            return "redirect:/forum/post/favfeed";
        }
        return "redirect:/forum/post/error";
    }

    // muting a post
    @PostMapping("/post/{id}/mute")
    public String mutePost(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        String result = favMutePostService.mutePost(id, userDetails);

        if (result.equals("redirect:/forum")) {
            return "redirect:/forum";
        } else {
            redirectAttributes.addFlashAttribute("MuteMessage", result);
            return "redirect:/forum";
        }
    }

    // show mute feed
    @GetMapping("/post/mutefeed")
    public String mutedpostfeed(Model model, @AuthenticationPrincipal UserDetails userDetails)
            throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByName(userDetails.getUsername());
        favMutePostService.mutedpostList = mutedPostRepository.findAllByUser(user.get());
        model.addAttribute("mutedpostList", favMutePostService.mutedpostList);
        model.addAttribute("commenterName", userDetails.getUsername());
        return "forum/mutePost";
    }

    // unmuting a post
    @PostMapping("/post/mutefeed/{postId}/delete")
    public String unmutePost(@PathVariable int postId, String commenterName) {
        return favMutePostService.unmutePost(postId, commenterName);
    }

    @Autowired
    private SortingPosts sortService;

    private String currentSortOrder = "";

    private List<Post> cachedPosts = null;

    @GetMapping("/sortPosts")
    public String sorted() {
        return "SortedPosts";
    }

    @GetMapping("/sort")
    public String showForumPage(Model model, @RequestParam(value = "sortFilter", required = false) String sortFilter,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "minLikes", required = false) Integer minLikes, HttpServletRequest request) {

        boolean refreshCache = "refreshClicked".equals(request.getParameter("refresh"));
        if (cachedPosts == null || refreshCache) {
            cachedPosts = (List<Post>) postRepository.findAll();
            currentSortOrder = "";
        }

        if ("filterByDateRange".equals(sortFilter) && startDateStr != null && endDateStr != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);
                cachedPosts = sortService.filterPostsByDateRange(cachedPosts, startDate, endDate);
                model.addAttribute("filterByDateRange", true);
                model.addAttribute("startDate", startDateStr);
                model.addAttribute("endDate", endDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if ("filterByMinLikes".equals(sortFilter) && minLikes != null) {
            cachedPosts = sortService.filterPostsByMinLikes(cachedPosts, minLikes);
            model.addAttribute("filterByMinLikes", true);
            model.addAttribute("minLikes", minLikes);
        }

        if ("sortByLikes".equals(sortFilter)) {
            toggleSortingOrder();
            cachedPosts = sortService.sortPostsByLikes(cachedPosts, currentSortOrder);
        } else if ("sortByTimestamp".equals(sortFilter)) {
            toggleSortingOrder();
            cachedPosts = sortService.sortPostsByTimestamp(cachedPosts, currentSortOrder);
        }
        List<Integer> likeList = new ArrayList<>();
        for (Post post : cachedPosts) {
            int numLikes = likeCRUDRepository.countByPostId(post.getId());
            likeList.add(numLikes);
        }

        model.addAttribute("likeCount", likeList);
        model.addAttribute("posts", cachedPosts);
        model.addAttribute("currentSortOrder", currentSortOrder);
        return "SortedPosts";
    }

    private void toggleSortingOrder() {
        currentSortOrder = "asc".equals(currentSortOrder) ? "desc" : "asc";
    }

	@Autowired
	private FavoriteAuthorRepository favoriteAuthorRepository;

	@Autowired
	private MutedAuthorRepository mutedAuthorRepository;

	@GetMapping("/userProfile")
	public String userProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());

		if (user.isPresent()) {
			User currentUser = user.get();
			List<User> authors = new ArrayList<>();
			userRepository.findAll().forEach(newuser -> authors.add(newuser));


			// Fetch and display the posts authored by the logged-in user
			List<Post> userPosts = postRepository.findAllByAuthorId(currentUser.getId());

			model.addAttribute("id", currentUser.getId());
			model.addAttribute("username", currentUser.getName());
			model.addAttribute("email", currentUser.getEmail());
			model.addAttribute("dateOfBirth", currentUser.getDateOfBirth());
			model.addAttribute("bio", currentUser.getBio());
			
			model.addAttribute("commenterName" , userDetails.getUsername());

			model.addAttribute("authors", authors);
			model.addAttribute("posts", userPosts); // Add the user's posts to the model
			
			model.addAttribute("user", user.orElse(null)); // user.orElse(null) handles the case when user is not present


			return "forum/userProfile";
		} else {
			// Handle the case when the user is not found
			return "redirect:/"; // Redirect to the homepage or handle it accordingly
		}
	}
	@PostMapping("/user/upload-profile-picture")
	public String postPic(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) {
	    // Retrieve the current user
	    Optional<User> userOptional = userRepository.findByName(userDetails.getUsername());

	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        try {
	            String fileName = file.getOriginalFilename();
	            File directory = new File(uploadDirectory);

	            if (!directory.exists()) {
	                directory.mkdirs();
	            }

	            Path filePath = Paths.get(uploadDirectory, fileName);

	            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	            // Update the user's imagePath
	            user.setImagePath(fileName);

	            // Save the updated user
	            userRepository.save(user);

	            System.out.println(user.getImagePath());
	        } catch (IOException e) {
	            // Handle the error appropriately, e.g., show an error message to the user
	            return "uploadError";
	        }
	    } else {
	        // Handle the case when the user is not found
	        return "redirect:/forum/userProfile"; // Redirect to the homepage or handle it accordingly
	    }

	    return "redirect:/forum/userProfile";
	}


	@GetMapping("/user/markFavorite")
	public String listOfFavoriteAuthors(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());
		List<User> favoriteAuthors = new ArrayList<>();
		List<FavoriteAuthor> favorites = favoriteAuthorRepository.findAllByUserId(user.get().getId());
		for (FavoriteAuthor f : favorites) {
			User newUser = userRepository.findById(f.getAuthor().getId()).get();
			favoriteAuthors.add(newUser);
		}
		model.addAttribute("favoriteAuthors", favoriteAuthors);
		return "forum/markFavorite";
	}

	@GetMapping("/user/markMuted")
	public String listAllMutedAuthors(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());

		if (user.isPresent()) {
			User currentUser = user.get();
			List<MutedAuthor> muted = mutedAuthorRepository.findAllByUserId(user.get().getId());
			List<User> mutedAuthors = new ArrayList<>();

			for (MutedAuthor m : muted) {
				User newUser = userRepository.findById(m.getAuthor().getId()).get();

				// Check if the author is also a favorite
				if (!isAuthorFavorite(currentUser, newUser)) {
					mutedAuthors.add(newUser);
				}
			}

			model.addAttribute("mutedAuthors", mutedAuthors);
		}

		return "forum/mutedAuthors";
	}

	@PostMapping("/user/markFavorite")
	public String markAuthorAsFavorite(@RequestParam("authorId") int authorId,
			@AuthenticationPrincipal UserDetails userDetails, Model model) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());

		if (user.isPresent()) {
			User currentUser = user.get();
			Optional<User> author = userRepository.findById(authorId);

			if (author.isPresent()) {
				User favoriteAuthor = author.get();

				// Check if the author is already a favorite
				if (!isAuthorFavorite(currentUser, favoriteAuthor)) {
					System.out.println("Here");
					FavoriteAuthor favorite = new FavoriteAuthor();
					favorite.setUser(currentUser);
					favorite.setAuthor(favoriteAuthor);
					String name = favorite.getUser().getName();
					model.addAttribute("favoriteAuthors", name);
					favoriteAuthorRepository.save(favorite);
				}
			}
		}

		return "redirect:/forum/user/markFavorite";
	}

	private boolean isAuthorFavorite(User currentUser, User author) {
		List<FavoriteAuthor> favorites = favoriteAuthorRepository.findByUserAndAuthor(currentUser, author);
		return !favorites.isEmpty();
	}

	@PostMapping("/user/markMuted")
	public String markAuthorAsMuted(@RequestParam("authorId") int authorId,
			@AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());

		if (user.isPresent()) {
			User currentUser = user.get();
			Optional<User> author = userRepository.findById(authorId);

			if (author.isPresent()) {
				User mutedAuthor = author.get();

				// Check if the author is already muted
				if (!isAuthorMuted(currentUser, mutedAuthor)) {
					MutedAuthor muted = new MutedAuthor();
					muted.setUser(currentUser);
					muted.setAuthor(mutedAuthor);
					mutedAuthorRepository.save(muted);
				}
			}
		}
		return "redirect:/forum/user/markMuted";
	}

	private boolean isAuthorMuted(User currentUser, User author) {
		List<MutedAuthor> mutedAuthors = mutedAuthorRepository.findByUserAndAuthor(currentUser, author);
		return !mutedAuthors.isEmpty();
	}

	@Transactional
	@PostMapping("/user/removeFavorite")
	public String removeFavoriteAuthor(@RequestParam("authorId") int authorId,
			@AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());
		System.out.println("going...");

		if (user.isPresent()) {
			User currentUser = user.get();
			Optional<User> author = userRepository.findById(authorId);

			if (author.isPresent()) {
				User favoriteAuthor = author.get();
				System.out.println(author);
				// Check if the author is a favorite
				if (isAuthorFavorite(currentUser, favoriteAuthor)) {
					// Remove the favorite
					favoriteAuthorRepository.deleteByUserAndAuthor(currentUser, favoriteAuthor);
//	                    favoriteAuthorRepository.save(del);

				}
			}
		}
		return "redirect:/forum/user/markFavorite";
	}

	@Transactional
	@PostMapping("/user/removeMuted")
	public String removeMutedAuthor(@RequestParam("authorId") int authorId,
			@AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> user = userRepository.findByName(userDetails.getUsername());
		System.out.println("going...");

		if (user.isPresent()) {
			User currentUser = user.get();
			Optional<User> author = userRepository.findById(authorId);

			if (author.isPresent()) {
				User mutedAuthor = author.get();
				System.out.println(author);
				// Check if the author is a favorite
				if (isAuthorMuted(currentUser, mutedAuthor)) {
					// Remove the favorite
					mutedAuthorRepository.deleteByUserAndAuthor(currentUser, mutedAuthor);
//	                    favoriteAuthorRepository.save(del);

				}
			}
		}
		return "redirect:/forum/user/markMuted";
	}
	@GetMapping("/goEdit")
	public String goToEdit() {
		return "redirect:/forum/editProfile";
	}

	@Transactional
	@GetMapping("/editProfile")
	public String editProfile(Model model) {
		model.addAttribute("registrationForm", new RegistrationForm());
		return "forum/editprofile";
	}
	@Transactional
	     @PostMapping("/editProfile")
	     public String postProfileDetails(@ModelAttribute("userForm") RegistrationForm registrationForm,@AuthenticationPrincipal UserDetails userDetails,BindingResult bindingResult,RedirectAttributes attr) {
	    	 if (bindingResult.hasErrors()) {
	 			attr.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
	 			attr.addFlashAttribute("registrationForm", registrationForm);
	 			return "redirect:/forum/editProfile";
	 		}
	    	 String username = userDetails.getUsername();
	    	 User user = domainUserService.getByName(username).get();
	    	 domainUserService.update(registrationForm.getUsername(), registrationForm.getBio(),registrationForm.getDateOfBirth(),user.getEmail());
	    	 return "redirect:/forum/userProfile";
	     }


}