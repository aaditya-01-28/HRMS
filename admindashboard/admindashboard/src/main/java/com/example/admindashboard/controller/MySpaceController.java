package com.example.admindashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.admindashboard.service.LearningDashboardService;
import java.security.Principal;
import java.util.List;
import java.time.LocalDate;
import com.example.admindashboard.model.Course;
import com.example.admindashboard.repository.CourseRepository;
import com.example.admindashboard.model.Training;
import com.example.admindashboard.repository.TrainingRepository;


@Controller
public class MySpaceController {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TrainingRepository trainingRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LearningDashboardService learningDashboardService;

    @GetMapping("/space/login")
    public String showLogin() {
        return "myspace-login";
    }
    @PostMapping("/space/authenticate")
    public String authenticate(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        User user = userRepository
                .findByUsername(username.toUpperCase())
                .orElse(null);

        if (user == null) {

            model.addAttribute("authError",
                    "Invalid username or password");

            return "myspace-login";
        }

        if (!passwordEncoder.matches(password,
                user.getPassword())) {

            model.addAttribute("authError",
                    "Invalid username or password");

            return "myspace-login";
        }

        String role = user.getRole().getRoleName();

        switch (role) {

            case "SENIOR_LND_HEAD":
                return "redirect:/space/lnd/dashboard";

            case "SENIOR_MANAGER":
                return "redirect:/space/manager/dashboard";

            case "SENIOR_HR":
                return "redirect:/senior_hr/employee";

            case "SENIOR_ACCOUNTS_HEAD":
                return "redirect:/space/accounts/dashboard";

            case "SENIOR_TRANSPORT_HEAD":
                return "redirect:/space/transport/dashboard";

            case "SENIOR_REWARDS_HEAD":
                return "redirect:/space/rewards/dashboard";

            default:

                model.addAttribute("authError",
                        "You are not authorized to access My Space");

                return "myspace-login";
        }
    }
    @GetMapping("/space/lnd/dashboard")
    public String lndDashboard(Model model,
                               Principal principal) {

        model.addAttribute(
                "stats",
                learningDashboardService.getDashboardStats());

        model.addAttribute(
                "activity",
                learningDashboardService.getLearningActivity());
        model.addAttribute(
                "enrollmentData",
                learningDashboardService.getEnrollmentTrend());
        model.addAttribute(
                "pendingApprovals",
                learningDashboardService.getPendingApprovals());
        model.addAttribute(
                "categoryData",
                learningDashboardService
                        .getCategoryDistribution());
        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        return "learning-admin-dashboard";
    }
    @GetMapping("/space/lnd/approvals")
    public String learningApprovals(Model model,
                                    Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        model.addAttribute(
                "approvals",
                learningDashboardService.getApprovalRequests());

        model.addAttribute(
                "approvalCount",
                learningDashboardService
                        .getApprovalRequests()
                        .size());

        return "learning-approvals";
    }
    
    @GetMapping("/space/lnd/course-addition")
    public String courseAddition(
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        List<Course> courses;

        if (courseType != null && !courseType.isBlank()
                && status != null && !status.isBlank()) {

            courses = courseRepository
                    .findByCourseTypeAndStatus(
                            courseType,
                            status);

        } else if (courseType != null
                && !courseType.isBlank()) {

            courses = courseRepository
                    .findByCourseType(courseType);

        } else if (status != null
                && !status.isBlank()) {

            courses = courseRepository
                    .findByStatus(status);

        } else if (search != null
                && !search.isBlank()) {

            courses = courseRepository
                    .findByCourseNameContainingIgnoreCase(search);

        } else {

            courses = courseRepository.findAll();
        }
        model.addAttribute(
                "providers",
                courseRepository.findAll()
                        .stream()
                        .map(Course::getCourseType)
                        .distinct()
                        .toList());

        model.addAttribute("courses", courses);

        return "learning-course-addition";
    }
    @GetMapping("/space/lnd/course-addition/new")
    public String addCoursePage(Model model,
                                Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if(user != null){
            model.addAttribute("loggedInUserName",
                    user.getFullName());

            model.addAttribute("loggedInEmail",
                    user.getEmail());
        }

        return "learning-add-course";
    }
    @PostMapping("/space/lnd/course/save")
    public String saveCourse(
            @RequestParam String courseName,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String level,
            @RequestParam Integer durationHours,
            @RequestParam String courseType,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        Course course = new Course();

        course.setCourseName(courseName);
        course.setDescription(description);
        course.setCategory(category);
        course.setLevel(level);
        course.setDurationHours(durationHours);
        course.setCourseType(courseType);

        course.setStatus("ACTIVE");

        course.setCreatedBy(user);

        courseRepository.save(course);

        return "redirect:/space/lnd/course-addition";
    }
    @GetMapping("/space/lnd/upcoming-training")
    public String upcomingTraining(

            @RequestParam(required = false)
            String trainingType,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String search,

            Model model,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (user != null) {

            model.addAttribute(
                    "loggedInUserName",
                    user.getFullName());

            model.addAttribute(
                    "loggedInEmail",
                    user.getEmail());
        }

        List<Training> trainings;

        if (trainingType != null
                && !trainingType.isBlank()
                && status != null
                && !status.isBlank()) {

            trainings =
                    trainingRepository
                            .findByTrainingTypeAndStatus(
                                    trainingType,
                                    status);

        } else if (trainingType != null
                && !trainingType.isBlank()) {

            trainings =
                    trainingRepository
                            .findByTrainingType(
                                    trainingType);

        } else if (status != null
                && !status.isBlank()) {

            trainings =
                    trainingRepository
                            .findByStatus(status);

        } else if (search != null
                && !search.isBlank()) {

            trainings =
                    trainingRepository
                            .findByTitleContainingIgnoreCase(
                                    search);

        } else {

            trainings =
                    trainingRepository.findAll();
        }

        model.addAttribute(
                "trainings",
                trainings);

        model.addAttribute(
                "trainingTypes",
                trainingRepository.findAll()
                        .stream()
                        .map(Training::getTrainingType)
                        .distinct()
                        .toList());

        return "learning-upcoming-training";
    }
    @PostMapping("/space/lnd/training/save")
    public String saveTraining(

            @RequestParam String title,

            @RequestParam String trainerName,

            @RequestParam String trainingType,

            @RequestParam LocalDate trainingDate,

            @RequestParam Integer duration,

            @RequestParam String description) {

        Training training = new Training();

        training.setTitle(title);

        training.setTrainerName(trainerName);

        training.setTrainingType(trainingType);

        training.setTrainingDate(trainingDate);

        training.setDuration(duration);

        training.setDescription(description);

        training.setStatus("SCHEDULED");

        trainingRepository.save(training);

        return "redirect:/space/lnd/upcoming-training";
    }
    
}