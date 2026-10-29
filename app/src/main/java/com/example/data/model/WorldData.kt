package com.example.data.model

data class CountryFlag(val name: String, val flag: String, val region: String)

data class LeagueInfo(val name: String, val badge: String, val country: String)

object BigSevenLeagues {
    val list = listOf(
        LeagueInfo("Premier League", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "England"),
        LeagueInfo("La Liga", "🇪🇸", "Spain"),
        LeagueInfo("Serie A", "🇮🇹", "Italy"),
        LeagueInfo("Bundesliga", "🇩🇪", "Germany"),
        LeagueInfo("Ligue 1", "🇫🇷", "France"),
        LeagueInfo("Primeira Liga", "🇵🇹", "Portugal"),
        LeagueInfo("Eredivisie", "🇳🇱", "Netherlands")
    )
}

object WorldFlags {
    // Complete world nations list with emojis, explicitly excluding Israel
    val countries: List<CountryFlag> = listOf(
        // Arab & Middle East / North Africa
        CountryFlag("Palestine", "🇵🇸", "Middle East"),
        CountryFlag("Morocco", "🇲🇦", "Africa"),
        CountryFlag("Algeria", "🇩🇿", "Africa"),
        CountryFlag("Egypt", "🇪🇬", "Africa"),
        CountryFlag("Tunisia", "🇹🇳", "Africa"),
        CountryFlag("Saudi Arabia", "🇸🇦", "Middle East"),
        CountryFlag("United Arab Emirates", "🇦🇪", "Middle East"),
        CountryFlag("Qatar", "🇶🇦", "Middle East"),
        CountryFlag("Kuwait", "🇰🇼", "Middle East"),
        CountryFlag("Oman", "🇴🇲", "Middle East"),
        CountryFlag("Bahrain", "🇧🇭", "Middle East"),
        CountryFlag("Iraq", "🇮🇶", "Middle East"),
        CountryFlag("Jordan", "🇯🇴", "Middle East"),
        CountryFlag("Lebanon", "🇱🇧", "Middle East"),
        CountryFlag("Syria", "🇸🇾", "Middle East"),
        CountryFlag("Yemen", "🇾🇪", "Middle East"),
        CountryFlag("Sudan", "🇸🇩", "Africa"),
        CountryFlag("Libya", "🇱🇾", "Africa"),
        CountryFlag("Mauritania", "🇲🇷", "Africa"),
        CountryFlag("Somalia", "🇸🇴", "Africa"),

        // Europe
        CountryFlag("England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "Europe"),
        CountryFlag("France", "🇫🇷", "Europe"),
        CountryFlag("Spain", "🇪🇸", "Europe"),
        CountryFlag("Germany", "🇩🇪", "Europe"),
        CountryFlag("Italy", "🇮🇹", "Europe"),
        CountryFlag("Portugal", "🇵🇹", "Europe"),
        CountryFlag("Netherlands", "🇳🇱", "Europe"),
        CountryFlag("Belgium", "🇧🇪", "Europe"),
        CountryFlag("Croatia", "🇭🇷", "Europe"),
        CountryFlag("Norway", "🇳🇴", "Europe"),
        CountryFlag("Sweden", "🇸🇪", "Europe"),
        CountryFlag("Denmark", "🇩🇰", "Europe"),
        CountryFlag("Poland", "🇵🇱", "Europe"),
        CountryFlag("Switzerland", "🇨🇭", "Europe"),
        CountryFlag("Austria", "🇦🇹", "Europe"),
        CountryFlag("Scotland", "🏴󠁧󠁢󠁳󠁣󠁴󠁿", "Europe"),
        CountryFlag("Wales", "🏴󠁧󠁢󠁷󠁬󠁳󠁿", "Europe"),
        CountryFlag("Ireland", "🇮🇪", "Europe"),
        CountryFlag("Northern Ireland", "🇬🇧", "Europe"),
        CountryFlag("Turkey", "🇹🇷", "Europe"),
        CountryFlag("Greece", "🇬🇷", "Europe"),
        CountryFlag("Czech Republic", "🇨🇿", "Europe"),
        CountryFlag("Hungary", "🇭🇺", "Europe"),
        CountryFlag("Romania", "🇷🇴", "Europe"),
        CountryFlag("Serbia", "🇷🇸", "Europe"),
        CountryFlag("Ukraine", "🇺🇦", "Europe"),
        CountryFlag("Slovakia", "🇸🇰", "Europe"),
        CountryFlag("Slovenia", "🇸🇮", "Europe"),
        CountryFlag("Finland", "🇫🇮", "Europe"),
        CountryFlag("Iceland", "🇮🇸", "Europe"),
        CountryFlag("Bosnia and Herzegovina", "🇧🇦", "Europe"),
        CountryFlag("Albania", "🇦🇱", "Europe"),
        CountryFlag("Bulgaria", "🇧🇬", "Europe"),
        CountryFlag("Georgia", "🇬🇪", "Europe"),
        CountryFlag("Cyprus", "🇨🇾", "Europe"),

        // South America (CONMEBOL)
        CountryFlag("Brazil", "🇧🇷", "South America"),
        CountryFlag("Argentina", "🇦🇷", "South America"),
        CountryFlag("Uruguay", "🇺🇾", "South America"),
        CountryFlag("Colombia", "🇨🇴", "South America"),
        CountryFlag("Chile", "🇨🇱", "South America"),
        CountryFlag("Ecuador", "🇪🇨", "South America"),
        CountryFlag("Paraguay", "🇵🇾", "South America"),
        CountryFlag("Peru", "🇵🇪", "South America"),
        CountryFlag("Venezuela", "🇻🇪", "South America"),
        CountryFlag("Bolivia", "🇧🇴", "South America"),

        // Africa (CAF)
        CountryFlag("Senegal", "🇸🇳", "Africa"),
        CountryFlag("Nigeria", "🇳🇬", "Africa"),
        CountryFlag("Ivory Coast", "🇨🇮", "Africa"),
        CountryFlag("Cameroon", "🇨🇲", "Africa"),
        CountryFlag("Ghana", "🇬🇭", "Africa"),
        CountryFlag("Mali", "🇲🇱", "Africa"),
        CountryFlag("Burkina Faso", "🇧🇫", "Africa"),
        CountryFlag("South Africa", "🇿🇦", "Africa"),
        CountryFlag("DR Congo", "🇨🇩", "Africa"),
        CountryFlag("Guinea", "🇬🇳", "Africa"),
        CountryFlag("Angola", "🇦🇴", "Africa"),
        CountryFlag("Zambia", "🇿🇲", "Africa"),
        CountryFlag("Gabon", "🇬🇦", "Africa"),
        CountryFlag("Cape Verde", "🇨🇻", "Africa"),
        CountryFlag("Togo", "🇹🇬", "Africa"),
        CountryFlag("Kenya", "🇰🇪", "Africa"),
        CountryFlag("Tanzania", "🇹🇿", "Africa"),
        CountryFlag("Uganda", "🇺🇬", "Africa"),
        CountryFlag("Ethiopia", "🇪🇹", "Africa"),

        // North & Central America (CONCACAF)
        CountryFlag("United States", "🇺🇸", "North America"),
        CountryFlag("Mexico", "🇲🇽", "North America"),
        CountryFlag("Canada", "🇨🇦", "North America"),
        CountryFlag("Jamaica", "🇯🇲", "North America"),
        CountryFlag("Costa Rica", "🇨🇷", "North America"),
        CountryFlag("Panama", "🇵🇦", "North America"),
        CountryFlag("Honduras", "🇭🇳", "North America"),
        CountryFlag("Trinidad and Tobago", "🇹🇹", "North America"),
        CountryFlag("Haiti", "🇭🇹", "North America"),

        // Asia & Oceania (AFC & OFC)
        CountryFlag("Japan", "🇯🇵", "Asia"),
        CountryFlag("South Korea", "🇰🇷", "Asia"),
        CountryFlag("Iran", "🇮🇷", "Asia"),
        CountryFlag("Australia", "🇦🇺", "Asia"),
        CountryFlag("Uzbekistan", "🇺🇿", "Asia"),
        CountryFlag("China", "🇨🇳", "Asia"),
        CountryFlag("India", "🇮🇳", "Asia"),
        CountryFlag("Indonesia", "🇮🇩", "Asia"),
        CountryFlag("Malaysia", "🇲🇾", "Asia"),
        CountryFlag("Thailand", "🇹🇭", "Asia"),
        CountryFlag("Vietnam", "🇻🇳", "Asia"),
        CountryFlag("Pakistan", "🇵🇰", "Asia"),
        CountryFlag("New Zealand", "🇳🇿", "Oceania"),
        CountryFlag("Fiji", "🇫🇯", "Oceania")
    )
}
