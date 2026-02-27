-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 27, 2026 at 04:22 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mindnest`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_user_dashboard_stats` (IN `p_user_id` INT)   BEGIN
    SELECT 
        (SELECT COUNT(*) FROM coaching_plan WHERE userId = p_user_id) as total_plans,
        (SELECT COUNT(*) FROM journal WHERE user_id = p_user_id) as total_journal_entries,
        (SELECT COUNT(*) FROM therapysession WHERE user_id = p_user_id AND session_status = 'Completed') as completed_sessions,
        (SELECT COUNT(*) FROM quiz_result WHERE user_id = p_user_id) as quizzes_taken;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Stand-in structure for view `active_users_summary`
-- (See below for the actual view)
--
CREATE TABLE `active_users_summary` (
`role` enum('PATIENT','THERAPIST','ADMIN')
,`total_users` bigint(21)
,`active_users` decimal(22,0)
);

-- --------------------------------------------------------

--
-- Table structure for table `coaching_plan`
--

CREATE TABLE `coaching_plan` (
  `planId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `title` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `goals` text NOT NULL,
  `image_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `coaching_plan`
--

INSERT INTO `coaching_plan` (`planId`, `userId`, `title`, `description`, `goals`, `image_path`) VALUES
(2, 1, 'ĺife', 'is hard', 'leken jamila', ''),
(3, 1, 'gtzrt', 'hjrts', 'ghres', ''),
(4, 1, 'hii', 'lll', 'kkl', ''),
(5, 1, 'yaaay', 'zfzkfztfk', 'jbjhvrhdrzf', 'C:\\Users\\elaka\\MindNest\\app_data\\images\\plans\\plan_f4d4dc02-a74c-42ee-b830-33b9b643cc18.jpg'),
(6, 1, 'cc', 'kkkk', 'lllllll', 'C:\\Users\\elaka\\MindNest\\app_data\\images\\plans\\plan_7e79694b-f252-43d3-b867-4196dcd54f3c.jpg'),
(7, 1, 'NNNNN', ';M;M;M', ';NM', NULL),
(8, 1, 'ggg', 'gggyou', 'ffff', 'C:\\Users\\GIGABYTE\\MindNest\\app_data\\images\\plans\\plan_7e2c1c9a-f465-4da0-b3eb-f21064bdffa1.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `comment`
--

CREATE TABLE `comment` (
  `id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `upvotes` int(11) DEFAULT 0,
  `downvotes` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `comment`
--

INSERT INTO `comment` (`id`, `content_id`, `text`, `created_at`, `upvotes`, `downvotes`) VALUES
(1, 8, 'yeye', '2026-02-14 17:29:56', 0, 0),
(2, 10, 'hewwo', '2026-02-14 17:42:58', 0, 0),
(3, 7, 'Top Cobain', '2026-02-14 18:32:53', 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `upvotes` int(11) DEFAULT 0,
  `upvoters_json` longtext DEFAULT NULL,
  `downvoters_json` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `content_id`, `user_id`, `text`, `created_at`, `upvotes`, `upvoters_json`, `downvoters_json`) VALUES
(1, 1, 101, 'This really helped me!', '2026-02-06 18:32:14', 2, NULL, NULL),
(2, 1, 1, 'suiiiii', '2026-02-11 21:49:39', 1, NULL, NULL),
(5, 3, 1, 'yeye ahh haircut', '2026-02-12 19:11:16', 0, NULL, NULL),
(6, 3, 1, 'helloooo', '2026-02-16 21:56:22', 0, NULL, NULL),
(8, 15, 1, 'hello', '2026-02-17 07:51:01', 3, NULL, NULL),
(9, 15, 8, 'hwo', '2026-02-21 15:43:38', 1, '[]', '[]'),
(10, 15, 8, 'hhhhhhhhhhhhhhhhhhhh', '2026-02-21 15:43:56', 0, '[]', '[8]'),
(11, 16, 7, 'great work¿¿', '2026-02-24 18:24:02', 1, '[7]', '[]'),
(12, 19, 7, 'yeah aight', '2026-02-24 19:02:02', 0, '[]', '[]'),
(13, 17, 11, 'hello everyone', '2026-02-24 19:48:02', 0, '[]', '[]');

-- --------------------------------------------------------

--
-- Table structure for table `content`
--

CREATE TABLE `content` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `type` varchar(50) NOT NULL,
  `source_url` varchar(400) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `image_url` varchar(2048) DEFAULT NULL,
  `upvotes` int(11) NOT NULL DEFAULT 0,
  `downvotes` int(11) NOT NULL DEFAULT 0,
  `week_key` varchar(16) DEFAULT NULL,
  `upvotes_week` int(11) NOT NULL DEFAULT 0,
  `downvotes_week` int(11) NOT NULL DEFAULT 0,
  `upvoters_json` longtext DEFAULT NULL,
  `downvoters_json` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `content`
--

INSERT INTO `content` (`id`, `title`, `description`, `type`, `source_url`, `category`, `created_at`, `image_url`, `upvotes`, `downvotes`, `week_key`, `upvotes_week`, `downvotes_week`, `upvoters_json`, `downvoters_json`) VALUES
(1, 'Dealing with Anxiety', 'A helpful video about anxiety.', 'Video', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwuDHqoSYu2mMMcmdOJkPrwdX6jNpqa7uyPQ&s', 'Habits', '2026-02-06 18:32:14', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwuDHqoSYu2mMMcmdOJkPrwdX6jNpqa7uyPQ&s', 0, 0, NULL, 0, 0, NULL, NULL),
(3, 'Heart & Soul', 'The official YouTube channel of Atlantic Records artist YoungBoy Never Broke Again. Subscribe for the latest music videos, performances, and more.', 'Article', 'https://youtu.be/3XijBN51Sd0?si=hj8p9xGCy7hFSyA4', 'Habits', '2026-02-11 21:53:54', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQb3Q4HkhgcF-p3KqucvRXkDqEBqIPV6bausg&s', 3, 0, NULL, 0, 0, NULL, NULL),
(5, 'Dealing with insomnia', 'meditation guide for your sleeping needs', 'Podcast', 'https://youtu.be/fe4XNbLWEs4?si=3jbvZKv3VmWwQmNE', 'Sleep', '2026-02-12 13:35:44', 'file:/C:/Users/GIGABYTE/Pictures/Screenshots/Screenshot%202026-01-23%20134308.png', 0, 0, NULL, 0, 0, NULL, NULL),
(6, 'yaya', 'baila a mi casa', 'Video', 'https://youtu.be/fe4XNbLWEs4?si=3jbvZKv3VmWwQmNE', 'Habits', '2026-02-12 13:36:45', 'file:/C:/Users/GIGABYTE/Pictures/Screenshots/Screenshot%202026-01-23%20134308.png', 1, 0, NULL, 0, 0, '[9]', '[]'),
(7, 'Images', 'image test', 'Article', 'https://en.wikipedia.org/wiki/Image', 'Mindset', '2026-02-13 15:43:49', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTc9APxkj0xClmrU3PpMZglHQkx446nQPG6lA&s', 9, 2, NULL, 0, 0, NULL, NULL),
(8, 'Top 10 Respect Moments', 'Top 10 Respect Moments in football', 'Podcast', 'https://youtu.be/1xAcS4xNj00?si=EY0Sy3d1H_7F-52H', 'Habits', '2026-02-14 14:32:39', 'https://i.ytimg.com/vi/1xAcS4xNj00/maxresdefault.jpg', 0, 1, NULL, 0, 0, '[]', '[8]'),
(9, 'Booty Warrior FREE!!', 'THE BOOTY WARRIOR IS FREE AND SHARING HIS STORY | Double Toasted - Today at Double Toasted we are discussing The Booty Warrior, who is now free from jail and telling his story. What do you think of this?', 'Article', 'https://youtu.be/m9GGOcpgij4?si=M9T1SqhWW870hiAD', 'Mindset', '2026-02-14 14:37:44', 'https://i.ytimg.com/vi/m9GGOcpgij4/sddefault.jpg', 8, 0, NULL, 0, 0, NULL, NULL),
(10, '\"It Hurted Me\" Fleece Johnson speaks on his Portrayal in the Boondocks Episode', '#fleecejohnson #boondocks #theboondocks\n#fleecejohnson #boondocks #theboondocks', 'Video', 'https://youtu.be/4SsKWuiOZSU?si=HgdmxqEvGke3El7p', 'Interview', '2026-02-14 14:46:10', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS-uymWcbgF0g0UT72lxHQOLejs6nbYJdptQQ&s', 3, 1, NULL, 0, 0, '[9]', '[]'),
(11, 'Atomic Habits: Tiny Changes, Remarkable Results (Key Ideas)', 'Practical habit-building ideas: identity-based habits, environment design, and small consistent improvements.', 'article', 'https://jamesclear.com/atomic-habits', 'Habits', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1506784365847-bbad939e9335', 142, 6, '2026-W07', 38, 1, '[8, 3]', '[]'),
(12, 'How to Practice Mindfulness (Basics + Simple Exercises)', 'Beginner-friendly guide to mindfulness: breathing, attention, and daily practice suggestions.', 'article', 'https://www.mindful.org/how-to-meditate/', 'Mindfulness', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1506126613408-eca07ce68773', 98, 4, '2026-W07', 21, 1, '[]', '[]'),
(13, 'What Is Cognitive Behavioral Therapy (CBT)?', 'Clear explanation of CBT principles and how thoughts, feelings, and behaviors interact.', 'article', 'https://www.apa.org/ptsd-guideline/patients-and-families/cognitive-behavioral', 'Mental Health', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1526256262350-7da7584cf5eb', 77, 4, '2026-W07', 17, 1, '[7]', '[3]'),
(14, 'The Science of Well-Being (Course Overview + Takeaways)', 'Free well-being insights and evidence-based practices (gratitude, savoring, habits).', 'article', 'https://www.coursera.org/learn/the-science-of-well-being', 'Well-being', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1499209974431-9dddcece7f88', 111, 7, '2026-W07', 29, 2, '[8]', '[]'),
(15, 'How to Make Stress Your Friend (Kelly McGonigal) — TED Talk', 'A mindset shift about stress and social connection; practical reframing.', 'video', 'https://www.ted.com/talks/kelly_mcgonigal_how_to_make_stress_your_friend', 'Stress', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee', 211, 12, '2026-W07', 54, 4, '[8]', '[]'),
(16, 'Inside the Mind of a Master Procrastinator (Tim Urban) — TED Talk', 'Why procrastination happens + how to deal with it using deadlines and awareness.', 'video', 'https://www.ted.com/talks/tim_urban_inside_the_mind_of_a_master_procrastinator', 'Productivity', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b', 187, 10, '2026-W07', 41, 3, '[7, 9]', '[]'),
(17, 'Huberman Lab Podcast (Andrew Huberman) — Show Page', 'test1', 'Article', 'https://www.hubermanlab.com/podcast', 'Performance', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1522071820081-009f0129c71c', 167, 9, '2026-W07', 36, 3, '[9, 11]', '[]'),
(18, 'The Happiness Lab with Dr. Laurie Santos — Podcast', 'Research-backed lessons on happiness, habits, and common thinking traps.', 'podcast', 'https://www.pushkin.fm/podcasts/the-happiness-lab-with-dr-laurie-santos', 'Happiness', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1496307653780-42ee777d4833', 153, 8, '2026-W07', 33, 2, '[9]', '[]'),
(19, 'The Tim Ferriss Show — Podcast', 'High-performance routines, mental models, and practical self-improvement experiments.', 'podcast', 'https://tim.blog/podcast/', 'Performance', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4', 137, 14, '2026-W07', 28, 2, '[]', '[]'),
(20, 'Sleep: Why It Matters + Healthy Sleep Tips', 'Basics of sleep health: consistency, light exposure, caffeine timing, and wind-down routines.', 'article', 'https://www.cdc.gov/sleep/about_sleep/sleep_hygiene.html', 'Sleep', '2026-02-16 22:28:20', 'https://images.unsplash.com/photo-1444703686981-a3abbc4d4fe3', 91, 6, '2026-W07', 19, 1, '[8]', '[9]');

-- --------------------------------------------------------

--
-- Table structure for table `exercise`
--

CREATE TABLE `exercise` (
  `exerciseId` int(11) NOT NULL,
  `planId` int(11) NOT NULL,
  `description` text NOT NULL,
  `duration` int(11) NOT NULL,
  `difficultyLevel` varchar(50) NOT NULL,
  `title` varchar(100) NOT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `exercise`
--

INSERT INTO `exercise` (`exerciseId`, `planId`, `description`, `duration`, `difficultyLevel`, `title`, `image`) VALUES
(2, 2, 'jkzhuc', 10, 'Easy', 'hft', ''),
(6, 5, 'jhjkmn', 10, 'Easy', 'jjjjjjggg', 'C:\\Users\\elaka\\OneDrive\\Images\\Saved Pictures\\illustration-anime-city.jpg'),
(7, 7, 'knhn', 1, 'Easy', 'mm', NULL),
(8, 8, 'mnn', 10, 'Medium', ',jbk', 'C:\\Users\\GIGABYTE\\Pictures\\Warframe\\Warframe0000.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `exercise_progress`
--

CREATE TABLE `exercise_progress` (
  `progressId` int(11) NOT NULL,
  `exerciseId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Not Started',
  `updatedAt` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `exercise_progress`
--

INSERT INTO `exercise_progress` (`progressId`, `exerciseId`, `userId`, `status`, `updatedAt`) VALUES
(8, 7, 1, 'In Progress', '2026-02-15 22:12:43'),
(10, 6, 1, 'In Progress', '2026-02-16 09:19:32');

-- --------------------------------------------------------

--
-- Table structure for table `journal`
--

CREATE TABLE `journal` (
  `id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `mood` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `journal`
--

INSERT INTO `journal` (`id`, `title`, `content`, `mood`, `created_at`) VALUES
(3, 'aa', 'aa', 'Happy 😊', '2026-02-16 11:11:00'),
(4, 'malek', 'hhh', '', '2026-02-16 13:11:33'),
(6, '', '', '', '2026-02-16 14:52:55'),
(7, 'emna', 'slkdnflkasmd', '', '2026-02-16 14:53:10'),
(8, '', '', 'aaaaaa', '2026-02-16 14:53:32'),
(9, '', '', 'aaa', '2026-02-16 14:59:32'),
(10, 'malek', 'saldknsdkl', 'asdnd;s', '2026-02-16 15:03:42'),
(11, 'malek', 'saldknsdkl', 'aaaaaaaaaaa', '2026-02-16 15:03:59'),
(12, 'gfxutr', 'gjfx', 'happy', '2026-02-16 15:34:08');

-- --------------------------------------------------------

--
-- Stand-in structure for view `popular_content`
-- (See below for the actual view)
--
CREATE TABLE `popular_content` (
`id` int(11)
,`title` varchar(255)
,`type` varchar(50)
,`category` varchar(100)
,`net_votes` bigint(12)
,`upvotes` int(11)
,`downvotes` int(11)
,`created_at` timestamp
);

-- --------------------------------------------------------

--
-- Table structure for table `profiles`
--

CREATE TABLE `profiles` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `goals` text DEFAULT NULL,
  `interests` text DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `profiles`
--

INSERT INTO `profiles` (`id`, `user_id`, `goals`, `interests`, `bio`, `created_at`) VALUES
(2, 4, NULL, 'haaaa', 'haaaa', '2026-02-17 04:33:09');

-- --------------------------------------------------------

--
-- Table structure for table `question`
--

CREATE TABLE `question` (
  `id` int(11) NOT NULL,
  `quiz_id` int(11) NOT NULL,
  `question_text` text DEFAULT NULL,
  `option_a` varchar(255) DEFAULT NULL,
  `option_b` varchar(255) DEFAULT NULL,
  `option_c` varchar(255) DEFAULT NULL,
  `option_d` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `question`
--

INSERT INTO `question` (`id`, `quiz_id`, `question_text`, `option_a`, `option_b`, `option_c`, `option_d`) VALUES
(2, 2, 'aaaaaaaaaa', '', '', '', ''),
(3, 3, 'kk', '', '', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `quiz`
--

CREATE TABLE `quiz` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiz`
--

INSERT INTO `quiz` (`id`, `title`, `description`, `category`) VALUES
(2, 'ads', 'sdfsad', 'dafs'),
(3, 'kkkkkkkkkk', 'kkkk\n', 'kkkkkkk'),
(5, 'tttt', 'jhgvuy', 'kjig');

-- --------------------------------------------------------

--
-- Table structure for table `quiz_result`
--

CREATE TABLE `quiz_result` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `quiz_id` int(11) NOT NULL,
  `score` int(11) NOT NULL DEFAULT 0,
  `total_questions` int(11) NOT NULL,
  `percentage` decimal(5,2) DEFAULT NULL,
  `completed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `quiz_result`
--
DELIMITER $$
CREATE TRIGGER `calculate_quiz_percentage` BEFORE INSERT ON `quiz_result` FOR EACH ROW BEGIN
    SET NEW.percentage = (NEW.score / NEW.total_questions) * 100;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `sessionfeedback`
--

CREATE TABLE `sessionfeedback` (
  `feedback_id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `rating` tinyint(4) DEFAULT NULL CHECK (`rating` between 1 and 5),
  `feedback_comment` text DEFAULT NULL,
  `feedback_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sessionfeedback`
--

INSERT INTO `sessionfeedback` (`feedback_id`, `session_id`, `patient_id`, `rating`, `feedback_comment`, `feedback_date`, `created_at`) VALUES
(1, 5, 1, 2, 'aight', '2026-02-14 23:39:02', '2026-02-14 23:39:02'),
(2, 6, 1, 3, 'kjhbkb', '2026-02-16 13:07:19', '2026-02-16 13:07:19');

-- --------------------------------------------------------

--
-- Table structure for table `therapysession`
--

CREATE TABLE `therapysession` (
  `session_id` int(11) NOT NULL,
  `psychologist_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `session_date` datetime NOT NULL,
  `duration_minutes` int(11) NOT NULL,
  `session_status` enum('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled',
  `session_notes` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `therapysession`
--

INSERT INTO `therapysession` (`session_id`, `psychologist_id`, `user_id`, `session_date`, `duration_minutes`, `session_status`, `session_notes`, `created_at`) VALUES
(1, 3, 999, '2026-02-05 08:00:00', 60, 'Completed', '', '2026-02-12 14:59:28'),
(4, 1, 999, '2026-01-30 13:00:00', 60, 'Scheduled', '', '2026-02-12 15:02:35'),
(5, 3, 1, '2026-02-05 09:30:00', 30, 'Completed', 'yeye', '2026-02-14 23:38:41'),
(6, 1, 1, '2026-01-27 08:00:00', 30, 'Completed', '', '2026-02-16 13:06:15'),
(7, 2, 1, '2027-02-18 14:30:00', 60, 'Scheduled', '', '2026-02-16 13:06:47');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(120) NOT NULL,
  `password` varchar(255) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` enum('Male','Female','Other') DEFAULT NULL,
  `role` enum('PATIENT','THERAPIST','ADMIN') DEFAULT 'PATIENT',
  `status` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `age`, `gender`, `role`, `status`, `created_at`) VALUES
(1, 'Admin User', 'admin@mindnest.com', 'admin123', 30, 'Other', 'ADMIN', 1, '2026-02-16 22:21:06'),
(2, 'Dr. Sarah Johnson', 'sarah@mindnest.com', 'therapist123', 35, 'Female', 'THERAPIST', 1, '2026-02-16 22:21:06'),
(3, 'John Patient', 'john@mindnest.com', 'patient123', 28, 'Male', 'PATIENT', 1, '2026-02-16 22:21:06'),
(4, 'yess', 'yess', 'yess', 12, 'Male', 'THERAPIST', 1, '2026-02-16 23:01:58'),
(5, '1', '1', '1', 21, 'Female', 'ADMIN', 1, '2026-02-17 01:16:43'),
(6, 'hamma', 'hamma@gmail.com', 'msm20042005', 21, 'Male', 'PATIENT', 1, '2026-02-17 04:41:17'),
(7, 'faozi', 'faozi@mindnest.com', '123456', 119, 'Male', 'PATIENT', 1, '2026-02-17 08:14:45'),
(8, 'rhamni', 'rhamni@mindnest.com', '123456', 21, 'Male', 'PATIENT', 1, '2026-02-21 15:13:42'),
(9, 'ahmeddd', 'ahmeddd@mindnest.com', '123456', 18, 'Male', 'PATIENT', 1, '2026-02-24 19:04:34'),
(10, 'tester', 'test@mindnest.com', '123456', 30, 'Male', 'PATIENT', 1, '2026-02-24 19:39:02'),
(11, 'jj', 'j@j.com', '123456', 19, 'Male', 'PATIENT', 1, '2026-02-24 19:46:26'),
(12, 'ahmedtest', 'ahmedtest@mindnest.com', '123456', 30, 'Female', 'PATIENT', 1, '2026-02-24 20:00:28');

-- --------------------------------------------------------

--
-- Table structure for table `user_answer`
--

CREATE TABLE `user_answer` (
  `id` int(11) NOT NULL,
  `quiz_id` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `selected_answer` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_answer`
--

INSERT INTO `user_answer` (`id`, `quiz_id`, `question_id`, `selected_answer`) VALUES
(2, 3, 3, ''),
(4, 2, 2, '');

-- --------------------------------------------------------

--
-- Table structure for table `user_content_events`
--

CREATE TABLE `user_content_events` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `event_type` varchar(30) NOT NULL,
  `weight` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_content_events`
--

INSERT INTO `user_content_events` (`id`, `user_id`, `content_id`, `event_type`, `weight`, `created_at`) VALUES
(1, 8, 18, 'VIEW', 1, '2026-02-22 01:03:29'),
(2, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:33'),
(3, 8, 18, 'VIEW', 1, '2026-02-22 01:03:35'),
(4, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:37'),
(5, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:39'),
(6, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:40'),
(7, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:41'),
(8, 8, 18, 'VIEW', 1, '2026-02-22 01:03:43'),
(9, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:45'),
(10, 8, 18, 'UPVOTE', 4, '2026-02-22 01:03:45'),
(11, 8, 19, 'VIEW', 1, '2026-02-22 01:03:50'),
(12, 8, 19, 'UPVOTE', 4, '2026-02-22 01:03:52'),
(13, 8, 11, 'VIEW', 1, '2026-02-22 01:03:56'),
(14, 8, 11, 'UPVOTE', 4, '2026-02-22 01:03:59'),
(15, 8, 18, 'VIEW', 1, '2026-02-22 01:04:08'),
(16, 8, 18, 'UPVOTE', 4, '2026-02-22 01:04:11'),
(17, 8, 18, 'UPVOTE', 4, '2026-02-22 01:04:12'),
(18, 8, 18, 'VIEW', 1, '2026-02-22 01:04:15'),
(19, 8, 18, 'VIEW', 1, '2026-02-22 01:04:18'),
(20, 8, 15, 'VIEW', 1, '2026-02-22 01:06:42'),
(21, 8, 15, 'UPVOTE', 4, '2026-02-22 01:06:44'),
(22, 8, 14, 'VIEW', 1, '2026-02-22 01:07:02'),
(23, 8, 14, 'UPVOTE', 4, '2026-02-22 01:07:04'),
(24, 8, 19, 'VIEW', 1, '2026-02-22 01:07:25'),
(25, 8, 19, 'UPVOTE', 4, '2026-02-22 01:07:27'),
(26, 8, 8, 'VIEW', 1, '2026-02-22 01:07:32'),
(27, 8, 8, 'DOWNVOTE', -2, '2026-02-22 01:07:34'),
(28, 8, 15, 'VIEW', 1, '2026-02-22 01:12:34'),
(29, 8, 18, 'VIEW', 1, '2026-02-22 01:12:42'),
(30, 8, 18, 'UPVOTE', 4, '2026-02-22 01:12:45'),
(31, 8, 18, 'UPVOTE', 4, '2026-02-22 01:12:46'),
(32, 8, 20, 'VIEW', 1, '2026-02-22 01:15:26'),
(33, 8, 20, 'UPVOTE', 4, '2026-02-22 01:15:28'),
(34, 8, 18, 'VIEW', 1, '2026-02-22 01:15:34'),
(35, 8, 18, 'UPVOTE', 4, '2026-02-22 01:15:35'),
(36, 8, 20, 'VIEW', 1, '2026-02-22 01:15:50'),
(37, 3, 16, 'VIEW', 1, '2026-02-22 01:17:44'),
(38, 3, 16, 'VIEW', 1, '2026-02-22 01:17:47'),
(39, 3, 16, 'OPEN_LINK', 2, '2026-02-22 01:17:49'),
(40, 3, 11, 'VIEW', 1, '2026-02-22 01:18:42'),
(41, 3, 11, 'UPVOTE', 4, '2026-02-22 01:18:44'),
(42, 3, 10, 'VIEW', 1, '2026-02-22 01:19:14'),
(43, 3, 18, 'VIEW', 1, '2026-02-22 01:19:27'),
(44, 3, 13, 'VIEW', 1, '2026-02-22 01:19:51'),
(45, 3, 13, 'VIEW', 1, '2026-02-22 01:19:58'),
(46, 3, 13, 'DOWNVOTE', -2, '2026-02-22 01:19:59'),
(47, 7, 15, 'VIEW', 1, '2026-02-24 08:24:02'),
(48, 7, 15, 'UPVOTE', 4, '2026-02-24 08:24:04'),
(49, 7, 14, 'VIEW', 1, '2026-02-24 08:24:07'),
(50, 7, 14, 'OPEN_LINK', 2, '2026-02-24 08:24:09'),
(51, 7, 13, 'VIEW', 1, '2026-02-24 08:24:34'),
(52, 7, 13, 'UPVOTE', 4, '2026-02-24 08:24:38'),
(53, 7, 12, 'VIEW', 1, '2026-02-24 08:24:45'),
(54, 7, 12, 'DOWNVOTE', -2, '2026-02-24 08:24:48'),
(55, 7, 12, 'DOWNVOTE', -2, '2026-02-24 08:24:49'),
(56, 7, 15, 'VIEW', 1, '2026-02-24 08:24:52'),
(57, 7, 15, 'UPVOTE', 4, '2026-02-24 08:24:54'),
(58, 7, 20, 'VIEW', 1, '2026-02-24 08:25:04'),
(59, 7, 20, 'UPVOTE', 4, '2026-02-24 08:25:06'),
(60, 7, 20, 'UPVOTE', 4, '2026-02-24 08:25:07'),
(61, 7, 15, 'VIEW', 1, '2026-02-24 08:26:07'),
(62, 7, 16, 'VIEW', 1, '2026-02-24 08:27:24'),
(63, 7, 16, 'OPEN_LINK', 2, '2026-02-24 08:27:26'),
(64, 7, 15, 'VIEW', 1, '2026-02-24 17:45:01'),
(65, 7, 16, 'VIEW', 1, '2026-02-24 17:55:58'),
(66, 7, 15, 'VIEW', 1, '2026-02-24 18:22:55'),
(67, 7, 15, 'UPVOTE', 4, '2026-02-24 18:23:00'),
(68, 7, 15, 'UPVOTE', 4, '2026-02-24 18:23:01'),
(69, 7, 16, 'VIEW', 1, '2026-02-24 18:23:45'),
(70, 7, 16, 'COMMENT', 3, '2026-02-24 18:24:02'),
(71, 7, 16, 'UPVOTE', 4, '2026-02-24 18:24:10'),
(72, 7, 16, 'UPVOTE', 4, '2026-02-24 18:24:13'),
(73, 7, 16, 'UPVOTE', 4, '2026-02-24 18:24:14'),
(74, 7, 16, 'OPEN_LINK', 2, '2026-02-24 18:24:15'),
(75, 7, 20, 'VIEW', 1, '2026-02-24 18:24:49'),
(76, 7, 17, 'VIEW', 1, '2026-02-24 19:01:36'),
(77, 7, 16, 'VIEW', 1, '2026-02-24 19:01:39'),
(78, 7, 15, 'VIEW', 1, '2026-02-24 19:01:42'),
(79, 7, 19, 'VIEW', 1, '2026-02-24 19:01:50'),
(80, 7, 19, 'COMMENT', 3, '2026-02-24 19:02:02'),
(81, 9, 15, 'VIEW', 1, '2026-02-24 19:05:54'),
(82, 9, 15, 'VIEW', 1, '2026-02-24 19:07:58'),
(83, 9, 20, 'UPVOTE', 4, '2026-02-24 19:36:08'),
(84, 9, 20, 'DOWNVOTE', -2, '2026-02-24 19:36:10'),
(85, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:24'),
(86, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:25'),
(87, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:25'),
(88, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:25'),
(89, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:27'),
(90, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:27'),
(91, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:27'),
(92, 9, 19, 'UPVOTE', 4, '2026-02-24 19:36:28'),
(93, 9, 18, 'UPVOTE', 4, '2026-02-24 19:36:29'),
(94, 9, 18, 'UPVOTE', 4, '2026-02-24 19:36:29'),
(95, 9, 18, 'UPVOTE', 4, '2026-02-24 19:36:29'),
(96, 9, 17, 'UPVOTE', 4, '2026-02-24 19:36:31'),
(97, 9, 17, 'UPVOTE', 4, '2026-02-24 19:36:31'),
(98, 9, 17, 'UPVOTE', 4, '2026-02-24 19:36:32'),
(99, 9, 16, 'UPVOTE', 4, '2026-02-24 19:36:37'),
(100, 9, 16, 'UPVOTE', 4, '2026-02-24 19:36:38'),
(101, 9, 16, 'UPVOTE', 4, '2026-02-24 19:36:38'),
(102, 9, 10, 'UPVOTE', 4, '2026-02-24 19:36:40'),
(103, 9, 6, 'UPVOTE', 4, '2026-02-24 19:36:41'),
(104, 9, 18, 'UPVOTE', 4, '2026-02-24 19:36:46'),
(105, 9, 18, 'VIEW', 1, '2026-02-24 19:45:25'),
(106, 9, 18, 'UPVOTE', 4, '2026-02-24 19:45:27'),
(107, 11, 17, 'VIEW', 1, '2026-02-24 19:47:50'),
(108, 11, 17, 'COMMENT', 3, '2026-02-24 19:48:02'),
(109, 11, 17, 'UPVOTE', 4, '2026-02-24 19:48:09'),
(110, 11, 17, 'OPEN_LINK', 2, '2026-02-24 19:48:12');

-- --------------------------------------------------------

--
-- Structure for view `active_users_summary`
--
DROP TABLE IF EXISTS `active_users_summary`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `active_users_summary`  AS SELECT `users`.`role` AS `role`, count(0) AS `total_users`, sum(case when `users`.`status` = 1 then 1 else 0 end) AS `active_users` FROM `users` GROUP BY `users`.`role` ;

-- --------------------------------------------------------

--
-- Structure for view `popular_content`
--
DROP TABLE IF EXISTS `popular_content`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `popular_content`  AS SELECT `content`.`id` AS `id`, `content`.`title` AS `title`, `content`.`type` AS `type`, `content`.`category` AS `category`, `content`.`upvotes`- `content`.`downvotes` AS `net_votes`, `content`.`upvotes` AS `upvotes`, `content`.`downvotes` AS `downvotes`, `content`.`created_at` AS `created_at` FROM `content` ORDER BY `content`.`upvotes`- `content`.`downvotes` DESC LIMIT 0, 10 ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `coaching_plan`
--
ALTER TABLE `coaching_plan`
  ADD PRIMARY KEY (`planId`);

--
-- Indexes for table `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_comment_content` (`content_id`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `content_id` (`content_id`);

--
-- Indexes for table `content`
--
ALTER TABLE `content`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `exercise`
--
ALTER TABLE `exercise`
  ADD PRIMARY KEY (`exerciseId`),
  ADD KEY `FK_plan` (`planId`);

--
-- Indexes for table `exercise_progress`
--
ALTER TABLE `exercise_progress`
  ADD PRIMARY KEY (`progressId`),
  ADD UNIQUE KEY `uq_ex_user` (`exerciseId`,`userId`),
  ADD KEY `idx_prog_user` (`userId`),
  ADD KEY `idx_prog_ex` (`exerciseId`);

--
-- Indexes for table `journal`
--
ALTER TABLE `journal`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `profiles`
--
ALTER TABLE `profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `question`
--
ALTER TABLE `question`
  ADD PRIMARY KEY (`id`),
  ADD KEY `quiz_id` (`quiz_id`);

--
-- Indexes for table `quiz`
--
ALTER TABLE `quiz`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `quiz_result`
--
ALTER TABLE `quiz_result`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `quiz_id` (`quiz_id`);

--
-- Indexes for table `sessionfeedback`
--
ALTER TABLE `sessionfeedback`
  ADD PRIMARY KEY (`feedback_id`),
  ADD KEY `session_id` (`session_id`);

--
-- Indexes for table `therapysession`
--
ALTER TABLE `therapysession`
  ADD PRIMARY KEY (`session_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_user_role_status` (`role`,`status`);

--
-- Indexes for table `user_answer`
--
ALTER TABLE `user_answer`
  ADD PRIMARY KEY (`id`),
  ADD KEY `quiz_id` (`quiz_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `user_content_events`
--
ALTER TABLE `user_content_events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user` (`user_id`),
  ADD KEY `idx_content` (`content_id`),
  ADD KEY `idx_time` (`created_at`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `coaching_plan`
--
ALTER TABLE `coaching_plan`
  MODIFY `planId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `comment`
--
ALTER TABLE `comment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `content`
--
ALTER TABLE `content`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `exercise`
--
ALTER TABLE `exercise`
  MODIFY `exerciseId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `exercise_progress`
--
ALTER TABLE `exercise_progress`
  MODIFY `progressId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `journal`
--
ALTER TABLE `journal`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `question`
--
ALTER TABLE `question`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `quiz`
--
ALTER TABLE `quiz`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `sessionfeedback`
--
ALTER TABLE `sessionfeedback`
  MODIFY `feedback_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `therapysession`
--
ALTER TABLE `therapysession`
  MODIFY `session_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `user_answer`
--
ALTER TABLE `user_answer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `user_content_events`
--
ALTER TABLE `user_content_events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=111;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `comment`
--
ALTER TABLE `comment`
  ADD CONSTRAINT `fk_comment_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `content` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exercise`
--
ALTER TABLE `exercise`
  ADD CONSTRAINT `FK_plan` FOREIGN KEY (`planId`) REFERENCES `coaching_plan` (`planId`);

--
-- Constraints for table `exercise_progress`
--
ALTER TABLE `exercise_progress`
  ADD CONSTRAINT `fk_prog_ex` FOREIGN KEY (`exerciseId`) REFERENCES `exercise` (`exerciseId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `question`
--
ALTER TABLE `question`
  ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `sessionfeedback`
--
ALTER TABLE `sessionfeedback`
  ADD CONSTRAINT `sessionfeedback_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `therapysession` (`session_id`);

--
-- Constraints for table `user_answer`
--
ALTER TABLE `user_answer`
  ADD CONSTRAINT `user_answer_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_answer_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
