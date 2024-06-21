package com.homework.todolist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * To do items repository implementation
 */
class TodoItemsRepositoryImpl : TodoItemsRepository {
    private val temporaryItems = mutableListOf(
        TodoItem(
            "1",
            "Необходимо покрасить забор",
            Importance.LOW,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(3)
        ),
        TodoItem(
            "2",
            "Необходимо закупиться продуктами на неделю",
            Importance.URGENT,
            true,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "3",
            "Нужно сходить в парикмахерскую",
            Importance.ORDINARY,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "4",
            "Купить порошок, сосиски, хлеб, горчицу. Не забыть покормить кота",
            Importance.URGENT,
            false,
            LocalDate.now().minusDays(33),
            LocalDate.now().plusDays(2),
            updateAt = LocalDateTime.now().plusMinutes(3)
        ),
        TodoItem(
            "5",
            "Сделать домашнюю работу по созданию Todo приложения",
            Importance.URGENT,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(4)
        ),
        TodoItem(
            "6",
            "Купить корм для гуппи",
            Importance.URGENT,
            true,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "7",
            "Позвонить стоматологу и назначить дату приема",
            Importance.ORDINARY,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "8",
            "Выучить китайский язык и перевести все деньги в юань",
            Importance.LOW,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "9",
            "Сделать очень длинный текст и посмотреть как он будет отображаться на форме редактирования задачи. Добавив этот текст быстро открыть вкладку в хроме и почитать информацию про сериал, который шел по телевизору в 2012 году на телеканале \"ТНТ\"",
            Importance.LOW,
            true,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "10",
            "Выучить английский язык и никогда не переводить деньги в доллар, в эту зеленую бумажку",
            Importance.URGENT,
            true,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),

        TodoItem(
            "11",
            "Создать задачу под номером 11, чтобы потом посмотреть ее на экране устройства",
            Importance.URGENT,
            true,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(3)
        ),
        TodoItem(
            "12",
            "Создать задачу под номером 12, чтобы потом сходить в магазин за хлебом, картошкой, ветчиной",
            Importance.LOW,
            false,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "13",
            "Создать очень длинную задачу под номером 13, чтобы потом посмотреть как обрезается текст на экране. Для того, чтобы он обрезался он должен быть очень большим. Бабочки, такие как голубянки (Lycaenidae), имеют нитевидные «хвосты» на концах крыльев и расположенные рядом глазчатые пятна, которые вместе образуют «ложную голову» — пятна имитируют глаза бабочки, а «хвосты» — усики-антенны. Эта аутомимикрия сбивает с толку хищников, таких как птицы и пауки-скакуны (Salticidae). Яркие примеры её встречаются у бабочек хвостаток; они обычно садятся вверх ногами с поднятой ложной головой и многократно двигают задними крыльями, создавая движения «хвостов» на их крыльях, похожие на движения усиков бабочки. Исследования повреждений заднего крыла подтверждают гипотезу о том, что такая тактика помогает избегать нападений на голову насекомого. Многие виды диких кошек, в том числе сервал, камышовый кот, пампасская кошка и кошка Жоффруа, имеют белые и черные пятна или полосы на задней стороне ушей. Предполагают, что они могут являться сигналом «следуй за мной» для их детёнышей. В этом случае может существовать эволюционный компромисс между ночным камуфляжем и внутривидовой передачей сигналов",
            Importance.ORDINARY,
            false,
            LocalDate.now(),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "14",
            "А задача 14 должна быть еще больше. Четырёхглазая рыба-бабочка имеет сплющенное по бокам тело. У основания хвоста имеется окаймлённое белым цветом чёрное пятно. Это «глазное пятно» является защитным приспособлением от зрительно ориентирующихся врагов. Хищные рыбы часто фокусируются во время преследования своей добычи на их глазах и таким образом вводятся в заблуждение, неправильно принимая во внимание направление их бегства. У мальков длиной до 3-х см имеется ещё одно дополнительное «глазное пятно» в задней области спинного плавника и 3 вертикальные полосы на теле. При этом чёрная полоса, которая тускнеет у взрослой особи, маскирует глаз. Две другие полосы шире и скорее коричневого цвета. Рыба достигает длины до 15 см, но чаще — до 10 см. Она живёт на глубинах от 1 до 20 м в Карибском море, Мексиканском заливе и на восточном побережье Соединенных Штатов, доходя на север до Массачусетса. В Карибском море и на островах Вест-Индии это самая частая рыба своего семейства. Молодь почти всегда живёт в небольших группах, взрослые рыбы — парами. Рыбы питаются водорослями, многощетинковыми червями, асцидиями, роговыми и другими кораллами. На рыбах-бабочках обнаружены питающиеся их кровью молодые особи равноногих рачков Gnathia marleyi",
            Importance.LOW,
            true,
            LocalDate.now().minusDays(11),
            LocalDate.now().plusDays(10),
            updateAt = LocalDateTime.now().plusMinutes(3)
        ),
        TodoItem(
            "15",
            "Задача 15 про глазчатого хвостокола. Широкие грудные плавники речных глазчатых хвостоколов срастаются с головой и образуют овальный диск. Спинные плавники и хвостовой плавник отсутствуют. Позади глаз расположены брызгальца. Брюшные плавники закруглены и почти полностью прикрыты диском. На вентральной стороне диска расположены ноздри и 5 пар жаберных щелей. Хвост довольно короткий и толстый по сравнению с другими представителями семейства речных хвостоколов. На его дорсальной поверхности имеется ядовитый шип. Каждые 6—12 месяцев он обламывается и на его месте вырастает новый. У основания шипа расположены железы, вырабатывающие яд, который распространяется по продольным канавкам. В обычном состоянии шип покоится в углублении из плоти, наполненном слизью и ядом. Окраска тела чаще серо-коричневого цвета с рисунком из жёлто-оранжевых глазков. Вентральная сторона диска белая. Молодые особи окрашены ярче взрослых. Максимальная зарегистрированная длина 100 см, а вес 15 кг. Дорсальная поверхность диска покрыта чешуёй",
            Importance.URGENT,
            false,
            LocalDate.now().minusDays(3),
            LocalDate.now().plusDays(4)
        ),
        TodoItem(
            "16",
            "Прочитать текст про костистых рыб. Костистые рыбы впервые появляются в среднем триасе, в мелу становятся уже многочисленными, а с кайнозойской эры распространяются повсеместно, образуя необычайное многообразие форм (больше 90 % ныне живущих видов рыб). Общими признаками костистых рыб служат костные чешуи (ганоидные были у некоторых вымерших), большая степень окостенения мозгового черепа (обычно есть верхняя затылочная кость), меньшее число костей в нижней челюсти (обычно 3), развиты костные лучи, поддерживающие кожистый край жаберной крышки. Хвостовой плавник гомоцеркальный. Артериальный конус редуцирован и функционально заменен луковицей аорты. В кишечнике отсутствует спиральный клапан. Плавательный пузырь лишен ячеистости на внутренних стенках; он связан каналом со спинной поверхностью начальной части пищевода; в онтогенезе эта связь может исчезать; у части видов плавательный пузырь вторично редуцируется.",
            Importance.LOW,
            false,
            LocalDate.now().plusYears(1),
            LocalDate.now().plusDays(2)
        ),
        TodoItem(
            "17",
            "Во́льфов кана́л, или вольфов проток (первичнопочечный канал; лат. ductus wolfii или ductus mesonephricus) — канал туловищной почки (мезонефроса) у позвоночных животных. Назван по имени естествоиспытателя Каспара Фридриха Вольфа. Вольфов канал у большинства позвоночных развивается из зачатка, растущего от пронефроса к клоаке. У эмбрионов и личинок земноводных и костных рыб вольфов канал служит выводным протоком пронефроса и мезонефроса; у половозрелых самок — только мезонефроса. У самцов земноводных существует связь между семенником и вольфовым каналом; последний одновременно функционирует и как мочеточник, и как семяпровод. У пресмыкающихся, птиц и млекопитающих в связи с появлением метанефроса со вторичным мочеточником вольфов канал функционирует только на ранних стадиях развития. Позже у самцов он становится семяпроводом, у самок редуцируется.",
            Importance.ORDINARY,
            true,
            LocalDate.now()
        ),
        TodoItem(
            "18",
            "Онтогене́з (от др.-греч. ὤν, лат. on > род. ὄντος, ontos «сущий» + γένεσις, genesis «зарождение») — индивидуальное развитие организма, совокупность последовательных морфологических и биохимических преобразований, претерпеваемых организмом от оплодотворения (при половом размножении) или от момента отделения от материнской особи (при бесполом размножении) до конца жизни.",
            Importance.LOW,
            false,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        )
    )

    private val _itemsFlow = MutableStateFlow<List<TodoItem>>(temporaryItems.toList())

    override fun getItemsList(): Flow<List<TodoItem>> = _itemsFlow.asStateFlow()

    override fun getItemDetails(id: TodoItemId): TodoItem? =
        _itemsFlow.value.find { it.id == id }

    override suspend fun removeItemById(id: TodoItemId) {
        temporaryItems.removeIf { it.id == id }
        _itemsFlow.value = temporaryItems.toList()
    }

    override fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate?
    ): TodoItemId {
        val newItem = TodoItem(
            id = "${temporaryItems.size + 1}",
            text = text,
            done = false,
            importance = importance,
            deadlineAt = deadlineAt
        )
        temporaryItems.add(newItem)
        _itemsFlow.value = temporaryItems.toList()
        return newItem.id
    }

    override suspend fun updateItem(
        id: TodoItemId,
        text: String,
        done: Boolean,
        importance: Importance,
        deadlineAt: LocalDate?,
        updated: LocalDateTime
    ): Boolean {
        val index = temporaryItems.indexOfFirst { it.id == id }
        return if (index != -1) {
            temporaryItems[index] = temporaryItems[index].copy(
                text = text,
                done = done,
                importance = importance,
                deadlineAt = deadlineAt,
                updateAt = updated
            )
            _itemsFlow.value = temporaryItems.toList()
            true
        } else {
            false
        }
    }

    override suspend fun updateItem(todoItem: TodoItem): Boolean {
        return updateItem(
            id = todoItem.id,
            text = todoItem.text,
            done = todoItem.done,
            importance = todoItem.importance,
            deadlineAt = todoItem.deadlineAt
        )
    }
}