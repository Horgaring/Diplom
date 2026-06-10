package com.horgaring.dateapp.data

object MockData {

    private const val PKG = "android.resource://com.horgaring.dateapp/drawable"
    private const val ME = "me"

    private val now: Long get() = System.currentTimeMillis()
    private fun minAgo(m: Long) = now - m * 60_000
    private fun hourAgo(h: Long) = now - h * 3_600_000

    val profiles: List<UserProfile> = listOf(
        UserProfile(
            id = "mock-1",
            name = "Анна Арматова",
            age = 18,
            bio = "Творческая натура. Рисую, пою, танцую. Мечтаю посетить Японию.",
            imageUrl = "$PKG/avatar_1",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Рисование", "Музыка", "Танцы", "Япония", "Аниме")
        ),
        UserProfile(
            id = "mock-2",
            name = "Света Лимонова",
            age = 20,
            bio = "Кофеман и книголюб. Обожаю долгие прогулки и разговоры по душам.",
            imageUrl = "$PKG/avatar_2",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Кофе", "Книги", "Прогулки", "Фотография")
        ),
        UserProfile(
            id = "mock-3",
            name = "Ольга Козенная",
            age = 19,
            bio = "Спортсменка и оптимистка. Бегаю по утрам, люблю йогу и здоровое питание.",
            imageUrl = "$PKG/avatar_3",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Бег", "Йога", "ЗОЖ", "Медитация")
        ),
        UserProfile(
            id = "mock-4",
            name = "Гульнара Фрогова",
            age = 18,
            bio = "Фотограф-любитель. Лучшие кадры получаются на закате. Ищу единомышленника.",
            imageUrl = "$PKG/avatar_4",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Фотография", "Закаты", "Природа", "Путешествия")
        ),
        UserProfile(
            id = "mock-5",
            name = "Тамила Соколова",
            age = 19,
            bio = "Учусь на дизайнера. Обожаю моду и красивые места для фото.",
            imageUrl = "$PKG/avatar_5",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Дизайн", "Мода", "Фото", "Искусство")
        ),
        UserProfile(
            id = "mock-6",
            name = "Дима Стульчиков",
            age = 20,
            bio = "Геймер и программист. Ищу девушку, которая разделит мои увлечения.",
            imageUrl = "$PKG/avatar_5",
            location = "Белгород",
            gender = "MALE",
            interests = listOf("Игры", "Программирование", "Кино", "Технологии")
        ),
        UserProfile(
            id = "mock-7",
            name = "Екатерина Волкова",
            age = 21,
            bio = "Обожаю активный отдых и путешествия. Летом была на Байкале — невероятно!",
            imageUrl = "$PKG/avatar_3",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Путешествия", "Байкал", "Походы", "Активный отдых", "Плавание")
        ),
        UserProfile(
            id = "mock-8",
            name = "Максим Иванов",
            age = 22,
            bio = "Музыкант и романтик. Играю на гитаре, пишу песни. Ищу музу.",
            imageUrl = "$PKG/avatar_1",
            location = "Белгород",
            gender = "MALE",
            interests = listOf("Гитара", "Музыка", "Песни", "Романтика")
        ),
        UserProfile(
            id = "mock-9",
            name = "Алина Морозова",
            age = 19,
            bio = "Люблю готовить и угощать. Лучшие десерты — мои! Хочу найти того, кто оценит.",
            imageUrl = "$PKG/avatar_2",
            location = "Белгород",
            gender = "FEMALE",
            interests = listOf("Кулинария", "Десерты", "Выпечка", "Итальянская кухня")
        ),
        UserProfile(
            id = "mock-10",
            name = "Артём Белов",
            age = 23,
            bio = "Заядлый путешественник, объехал пол-России. Мечтаю о кругосветке.",
            imageUrl = "$PKG/avatar_4",
            location = "Белгород",
            gender = "MALE",
            interests = listOf("Путешествия", "Россия", "Приключения", "Кругосветка", "Походы")
        )
    )

    private val anna = profiles[0]

    val chatRooms: List<ChatConversation> = listOf(
        ChatConversation(
            id = "chat-1",
            match = Match(
                id = "match-1",
                user = anna,
                matchedAt = hourAgo(48)
            ),
            lastMessage = "тест",
            lastMessageTime = minAgo(15),
            unreadCount = 1,
            messages = listOf(
                Message(
                    id = "msg-1",
                    senderId = anna.id,
                    receiverId = ME,
                    text = "тест",
                    timestamp = minAgo(15),
                    isFromMe = false
                )
            )
        )
    )
}
