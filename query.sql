use moviedb;


select t.* from (
	select distinct count(*) OVER()  AS total_rows, movies.id as movie_id, movies.title, movies.year, movies.director, rating, 
	group_concat(distinct genres.name SEPARATOR ', ') AS listGenres, 
	group_concat(distinct stars.id ORDER BY stars.name separator ', ') AS listStarsId,
	group_concat(distinct stars.name SEPARATOR ', ') as listStarsName
	from ratings, movies
	inner join genres_in_movies on movies.id = genres_in_movies.movieId
	inner join genres on genres.id = genres_in_movies.genreId
	inner join stars_in_movies on movies.id = stars_in_movies.movieId 
	inner join stars on stars.id = stars_in_movies.starId
	where ratings.movieId = movies.id AND movies.title like '%love%'
	group by movies.id
limit 10 offset 0
) AS t 
 order by t.rating asc
;


-- SELECT stars.id as star_id, movies.id as movie_id, movies.title, movies.year, movies.director,
-- group_concat(distinct genres.name SEPARATOR ', ') AS listGenres, 
-- group_concat(distinct stars.name SEPARATOR ', ') AS listStars, ratings.rating 
-- FROM ratings, movies
-- inner join genres_in_movies on genres_in_movies.movieId = movies.id
-- inner join genres on genres.id = genres_in_movies.genreId
-- inner join stars_in_movies on movies.id = stars_in_movies.movieId 
-- inner join stars on stars.id = stars_in_movies.starId
-- WHERE ratings.movieId = movies.id AND movies.id = ?
-- group by movies.id;


-- SELECT stars.name, stars.birthYear, 
-- group_concat(distinct movies.title SEPARATOR ', ') AS listMoviesTitle,
-- group_concat(distinct movies.id ORDER BY movies.title SEPARATOR ', ') AS listMoviesId
-- FROM stars
-- INNER JOIN stars_in_movies ON stars_in_movies.starId = stars.id
-- INNER JOIN movies ON stars_in_movies.movieId = movies.id
-- WHERE stars.id = ?
-- group by stars.id;