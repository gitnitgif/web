<template>
  <div class="city-detail-container">
    <div class="city-header">
      <button @click="goBack" class="back-btn">← 返回</button>
      <h1>{{ cityInfo.name }}方言</h1>
    </div>

    <div class="city-content">
      <div class="city-info-card">
        <h2>{{ cityInfo.name }}话</h2>
        <div class="city-tag">{{ cityInfo.category }}</div>
        
        <div class="info-section">
          <h3>方言简介</h3>
          <p>{{ cityInfo.description }}</p>
        </div>

        <div class="info-section">
          <h3>分布地区</h3>
          <p>{{ cityInfo.distribution }}</p>
        </div>

        <div class="info-section">
          <h3>语音特点</h3>
          <ul>
            <li v-for="(feature, index) in cityInfo.features" :key="index">
              {{ feature }}
            </li>
          </ul>
        </div>

        <div class="info-section">
          <h3>常用词汇</h3>
          <div class="vocabulary-grid">
            <div 
              v-for="(word, index) in cityInfo.vocabulary" 
              :key="index" 
              class="vocabulary-item"
            >
              <div class="word-chinese">{{ word.chinese }}</div>
              <div class="word-dialect">{{ word.dialect }}</div>
              <div class="word-pinyin">{{ word.pinyin }}</div>
            </div>
          </div>
        </div>

        <div class="info-section" v-if="cityInfo.examples">
          <h3>例句展示</h3>
          <div class="examples-container">
            <div 
              v-for="(example, index) in cityInfo.examples" 
              :key="index" 
              class="example-item"
            >
              <div class="example-dialect">{{ example.dialect }}</div>
              <div class="example-chinese">{{ example.chinese }}</div>
            </div>
          </div>
        </div>

        <div class="info-section" v-if="cityInfo.culture">
          <h3>文化背景</h3>
          <p>{{ cityInfo.culture }}</p>
        </div>
      </div>
      <div class="city-info-card">
        
        <div class="culture-gallery" v-if="cityInfo.images && cityInfo.images.length > 0">
          <h3>文化图片</h3>
          <div class="gallery-container" ref="imageContainerRef">
            <div class="images-wrapper">
              <img 
                v-for="(img, index) in cityInfo.images" 
                :key="index"
                :src="img" 
                :alt="`${cityInfo.name}文化图片${index + 1}`" 
                class="culture-image"
              />
            </div>
            <div class="gallery-nav">
              <button @click="prevImage" class="nav-btn prev">‹</button>
              <div class="indicators">
                <span 
                  v-for="(img, index) in cityInfo.images" 
                  :key="index"
                  :class="['indicator', { active: index === currentImageIndex }]"
                  @click="goToImage(index)"
                ></span>
              </div>
              <button @click="nextImage" class="nav-btn next">›</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { gsap } from 'gsap'

const router = useRouter()
const route = useRoute()

// 图片滚动相关
const imageContainerRef = ref(null)
const imagesRef = ref([])
const currentImageIndex = ref(0)
let autoPlayTimer = null

const cityData = {
  fuzhou: {
    name: '福州',
    category: '闽东语',
    description: '福州话是闽东语的代表方言，主要通行于福建省福州市及其周边地区。福州话保留了大量古汉语的语音特点，是研究古汉语的重要活化石。',
    distribution: '福州市区、闽侯、长乐、福清、连江、罗源、闽清、永泰、平潭等',
    features: [
      '声母系统保留古汉语特点',
      '韵母丰富，有入声韵尾',
      '声调有 7 个，保留入声',
      '连读变调复杂',
      '有丰富的文白异读现象'
    ],
    vocabulary: [
      { chinese: '你', dialect: '汝', pinyin: 'nǚ' },
      { chinese: '他', dialect: '伊', pinyin: 'ĭ' },
      { chinese: '什么', dialect: '甚毛', pinyin: 'sièng-mò̤' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'siĕh-uâng' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'dŏ̤h-siâ' },
      { chinese: '漂亮', dialect: '好看', pinyin: 'hō̤-káung' }
    ],
    examples: [
      { dialect: '汝今旦有食饭无？', chinese: '你今天吃饭了吗？' },
      { dialect: '我卜去福州。', chinese: '我要去福州。' }
    ],
    culture: '福州话属于闽语闽东片（侯官片），是闽越文化与中原南迁汉人长期融合形成的古老方言，历史可追溯至秦汉闽越时期，历经两晋、唐末、两宋三次大规模中原士族入闽，不断吸收古汉语语音、词汇并与闽越土著语言融合，逐渐定型为独具特色的地方方言。作为福州及闽东地区的通用语，它不仅是当地民众日常交流的工具，更承载着闽都千年的文化底蕴，深深融入闽剧、评话、伬唱、十番音乐等传统艺术，同时伴随福州人漂洋过海，成为海外福州籍华侨重要的文化纽带与身份标识。福州话保留了大量上古、中古汉语特征，被视作研究古汉语的活化石，在民俗、宗族、节庆等社会生活中扮演核心角色，虽受普通话普及影响使用场景有所缩减，但仍是闽都文化不可或缺的重要组成部分，也是福建方言体系中极具代表性的一支。',
    images:[
      '/culture/fuzhou1.jpg',
      '/culture/fuzhou2.jpg',
      '/culture/fuzhou3.jpg',
      '/culture/fuzhou4.jpg',
      '/culture/fuzhou5.jpg'
    ]

  },
  xiamen: {
    name: '厦门',
    category: '闽南语',
    description: '厦门话是闽南语的重要代表方言之一，广泛通行于厦门市、漳州市部分地区以及台湾。厦门话在海外华人社区也有广泛使用，是连接两岸同胞的重要语言纽带。',
    distribution: '厦门市、漳州部分地区、台湾部分地区',
    features: [
      '保留古汉语的浊塞音和浊擦音',
      '有 15 个声母，75 个韵母',
      '声调有 7-8 个',
      '有丰富的连读变调',
      '文白异读现象普遍'
    ],
    vocabulary: [
      { chinese: '你', dialect: '你', pinyin: 'lí' },
      { chinese: '他', dialect: '伊', pinyin: 'i' },
      { chinese: '什么', dialect: '啥物', pinyin: 'siáⁿ-mi̍h' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'chia̍h-pn̄g' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'to-siā' },
      { chinese: '漂亮', dialect: '水', pinyin: 'súi' }
    ],
    examples: [
      { dialect: '你有食饱未？', chinese: '你吃饱了吗？' },
      { dialect: '我欲去厦门。', chinese: '我要去厦门。' }
    ],
    culture: '厦门话属于闽南语泉漳片，以泉州音与漳州音交融为基础，是闽南语的核心代表方言之一，其形成源自中原汉人多次南迁，将上古、中古汉语带入闽地，并与当地土著语言逐渐融合，在明清时期随着厦门口岸的兴起定型为独立且影响力巨大的地方口音。作为闽南文化的重要载体，厦门话不仅是厦漳泉地区民间交流、宗族传承、民俗节庆的通用语言，还深深扎根于歌仔戏、南音、高甲戏等传统艺术之中，同时伴随闽南人大规模移居台湾及东南亚，成为台湾通用闽南语的重要源头，也是海外闽南侨民维系乡情、传承文化的核心纽带。厦门话保留了丰富的古汉语词汇与语音特征，是研究古汉语和闽南方言演变的活化石，近代以来更因厦门作为通商口岸而兼具海洋文化与商贸特色，如今虽受普通话普及影响，但依然是闽南文化圈的重要象征，在地方文化传承、两岸及海外交流中持续发挥着不可替代的作用。',
    images: [
      '/culture/xiamen1.jpg',
      '/culture/xiamen2.jpg',
      '/culture/xiamen3.jpg',
      '/culture/xiamen4.jpg'
    ]
  },
  quanzhou: {
    name: '泉州',
    category: '闽南语',
    description: '泉州话是闽南语的重要分支，主要通行于泉州市及其周边地区。作为古代海上丝绸之路的起点，泉州话保留了大量中古汉语的特点。',
    distribution: '泉州市区、晋江、石狮、南安、惠安、安溪、永春、德化等',
    features: [
      '保留完整的入声韵尾',
      '声调系统复杂，有 7-8 个声调',
      '文白异读丰富',
      '保留古汉语词汇较多'
    ],
    vocabulary: [
      { chinese: '你', dialect: '你', pinyin: 'lú' },
      { chinese: '他', dialect: '伊', pinyin: 'i' },
      { chinese: '什么', dialect: '甚乜', pinyin: 'siám-mih' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'tsia̍h-puīⁿ' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'to-siā' },
      { chinese: '漂亮', dialect: '媠', pinyin: 'suí' }
    ],
    examples: [
      { dialect: '你有食饱未？', chinese: '你吃饱了吗？' },
      { dialect: '我卜去泉州。', chinese: '我要去泉州。' }
    ],
    culture: '泉州话是闽南语的核心源头与代表口音，源自晋唐以来中原汉人大规模南迁，带来的上古、中古汉语与闽越土著语言长期融合而成，自唐宋起便随泉州港作为“东方第一大港”的繁荣而趋于成熟稳定，是闽南语中历史最悠久、底蕴最深厚的一支。作为古闽地文化、海洋商贸文化与中原士族文化的重要载体，泉州话不仅是泉州本土民众日常生活、宗族礼仪、民俗信仰的核心语言，更深度融入南音、梨园戏、提线木偶、拍胸舞等非遗艺术之中，同时随着宋元海上丝绸之路的贸易往来以及明清移民浪潮，深刻影响了漳州话、厦门话、台湾闽南语及东南亚各国闽南方言，成为海内外闽南族群的文化根脉与身份象征。泉州话完整保留了大量古汉语的语音、词汇与语法特征，被誉为古汉语活化石，承载着刺桐港千年的商贸记忆与海洋精神，至今仍是闽南文化圈的重要根基，在维系乡土认同、传承非遗文化、联结海内外侨亲等方面依旧具有不可替代的地位与影响力。',
    images: [
      '/culture/quanzhou1.jpg',
      '/culture/quanzhou2.jpg',
      '/culture/quanzhou3.jpg',
      '/culture/quanzhou4.jpg'
    ]
  },
  zhangzhou: {
    name: '漳州',
    category: '闽南语',
    description: '漳州话是闽南语的重要分支，主要通行于漳州市及其周边地区。漳州话音调较为平缓，具有独特的语音特色。',
    distribution: '漳州市区、龙海、漳浦、云霄、诏安、东山、平和、南靖、华安、长泰等',
    features: [
      '声调相对平缓',
      '保留古汉语入声',
      '有丰富的鼻化韵',
      '连读变调规律独特'
    ],
    vocabulary: [
      { chinese: '你', dialect: '你', pinyin: 'lí' },
      { chinese: '他', dialect: '伊', pinyin: 'i' },
      { chinese: '什么', dialect: '甚乜', pinyin: 'siám-mih' },
      { chinese: '吃饭', dialect: '食糜', pinyin: 'tsia̍h-muâi' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'to-siā' },
      { chinese: '漂亮', dialect: '水', pinyin: 'súi' }
    ],
    examples: [
      { dialect: '你有食饱未？', chinese: '你吃饱了吗？' },
      { dialect: '我卜去漳州。', chinese: '我要去漳州。' }
    ],
    culture: '漳州话属于闽南语漳泉片的漳州音系，是闽南语重要支系，其形成源于秦汉至唐宋中原汉人持续南迁，带来上古、中古汉语语音词汇，并与闽越土著语言长期融合，在唐代陈元光开漳后逐步定型，成为漳州本土核心方言。作为漳州千年文化的载体，它深度融入芗剧、潮剧、锦歌、南音、竹马戏等地方戏曲与民间艺术，贯穿岁时节庆、宗族礼仪、民间信仰等日常生活，同时因漳州历史上商贸发达、移民众多，随着海上丝绸之路与下南洋移民浪潮，广泛传播至台湾南部及东南亚各地，是台湾闽南语与海外闽南侨乡方言的重要源头之一。漳州话保留了大量古汉语特征与独特的闽越底层词汇，兼具农耕文明与海洋商贸文化印记，既是研究古汉语和闽南文化演变的活化石，也是维系海内外漳州人乡情与文化认同的重要纽带，如今虽受普通话普及影响，但仍在民间传承、民俗活动中保持活力，是闽南文化体系中极具地域特色的重要组成部分。',
    images: [
      '/culture/zhangzhou1.jpg',
      '/culture/zhangzhou2.jpg',
      '/culture/zhangzhou3.jpg',
      '/culture/zhangzhou4.jpg',
      '/culture/zhangzhou5.jpg'
    ]
  },
  putian: {
    name: '莆田',
    category: '闽语',
    description: '莆田话（兴化话）是闽语的重要分支，主要通行于莆田市及其周边地区。莆田话有独特的语音系统，与闽东语、闽南语都有明显差异。',
    distribution: '莆田市区、仙游等',
    features: [
      '声母系统独特',
      '韵母丰富',
      '声调有 7 个',
      '保留古汉语特点',
      '有独特的变调规律'
    ],
    vocabulary: [
      { chinese: '你', dialect: '汝', pinyin: 'tṳ̄' },
      { chinese: '他', dialect: '伊', pinyin: 'i' },
      { chinese: '什么', dialect: '甚乜', pinyin: 'sáng-măh' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'sé̤ṳ-uáng' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'dŏ̤-siâ' },
      { chinese: '漂亮', dialect: '雅', pinyin: 'ngā' }
    ],
    examples: [
      { dialect: '汝今旦有食饭无？', chinese: '你今天吃饭了吗？' },
      { dialect: '我卜去莆田。', chinese: '我要去莆田。' }
    ],
    culture: '莆田话（莆仙话）属于闽语莆仙片，是介于闽东语与闽南语之间的独立方言，形成源于魏晋至唐宋中原汉人南迁，在长期相对封闭的地理环境中，融合古闽越土著语言、中古汉语以及周边闽语特点独立发展而成，历史底蕴极为古老。作为莆田、仙游两地的核心母语，它深度根植于莆田重教兴学、科举鼎盛的文化传统，完整承载了莆仙戏、妈祖信仰、元宵民俗、宗族礼仪等地方文化精髓，尤其是莆仙戏的念白与唱腔更以莆田话为根基，被誉为宋元南戏的活化石。由于地处闽东与闽南交界且长期自成一体，莆田话语音、词汇独具一格，与周边方言互通度极低，保留了大量古汉语遗存与独特的本土词汇，同时随着莆田人外出经商、移民海外，也成为海内外莆仙侨民维系乡情、传承乡土文化的重要纽带，如今虽受通用语普及影响传承压力渐增，但仍是莆仙地域文化最核心的标志与精神纽带。',
    images: [
      '/culture/putian1.jpg',
      '/culture/putian2.jpg',
      '/culture/putian3.jpg',
      '/culture/putian4.jpg',
      '/culture/putian5.jpg',
      '/culture/putian6.jpg'
    ]
  },
  longyan: {
    name: '龙岩',
    category: '客家语',
    description: '龙岩话属于客家语系，主要通行于龙岩市及其周边地区。龙岩话保留了客家话的典型特征，同时受到闽语的影响。',
    distribution: '龙岩市区、永定、上杭、武平、长汀、连城、漳平等',
    features: [
      '保留客家话典型特征',
      '声调有 6 个',
      '保留古入声',
      '受闽语影响有一定变化'
    ],
    vocabulary: [
      { chinese: '你', dialect: '你', pinyin: 'ngì' },
      { chinese: '他', dialect: '佢', pinyin: 'kì' },
      { chinese: '什么', dialect: '脉个', pinyin: 'mak-ke' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'sit-fan' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'dô-tsiâ' },
      { chinese: '漂亮', dialect: '靓', pinyin: 'liàng' }
    ],
    examples: [
      { dialect: '你有食饱无？', chinese: '你吃饱了吗？' },
      { dialect: '涯爱去龙岩。', chinese: '我要去龙岩。' }
    ],
    culture: '龙岩话属于闽南语泉漳片的龙岩小片，通行于新罗、漳平一带，是受客家文化深度影响的闽南语变体，其形成源于早期闽南移民沿九龙江上游进入闽西定居，带来漳州、泉州一带方言，并在与周边客家民系长期杂居交融的过程中逐渐演变，既保留了闽南语的核心基底，又吸收了客家话的语音、词汇与习俗，形成独具特色的方言形态。作为龙岩城区及周边的本土方言，它承载着闽西地区闽南与客家文化交汇融合的历史印记，融入地方民间歌谣、戏曲、庙会信仰与宗族礼仪之中，既是当地民众日常交流、维系乡情的语言纽带，也见证了闽西作为闽南文化与客家文化过渡地带的独特历史进程。龙岩话既保留部分古汉语特征，又兼具闽南与客家双重文化特质，在方言格局上呈现出明显的过渡性与包容性，虽在普通话普及下使用人群有所缩减，但仍是闽西地域文化的重要标识，也是研究闽南与客家方言接触、文化交融的珍贵样本。',
    images: [
      '/culture/longyan1.jpg',
      '/culture/longyan2.jpg',
      '/culture/longyan3.jpg',
      '/culture/longyan4.jpg'
    ]
  },
  sanming: {
    name: '三明',
    category: '闽中语',
    description: '三明话属于闽中语，主要通行于三明市及其周边地区。三明话是闽语的重要分支，具有独特的语音特点。',
    distribution: '三明市区、永安、沙县、尤溪、大田等',
    features: [
      '声母系统独特',
      '韵母丰富',
      '声调有 6-7 个',
      '保留古汉语特点'
    ],
    vocabulary: [
      { chinese: '你', dialect: '你', pinyin: 'nǐ' },
      { chinese: '他', dialect: '伊', pinyin: 'yī' },
      { chinese: '什么', dialect: '甚毛', pinyin: 'siáng-mó̤' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'sié-uáng' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'dó̤-siâ' },
      { chinese: '漂亮', dialect: '好睇', pinyin: 'hó̤-tā̤' }
    ],
    examples: [
      { dialect: '你有食饱未？', chinese: '你吃饱了吗？' },
      { dialect: '我卜去三明。', chinese: '我要去三明。' }
    ],
    culture: '三明话（通常指以永安话、沙县话为核心的闽中语）是闽语五大分支中使用范围最窄、特色极强的一支，其形成深植于闽中山区千年的地理阻隔、移民交融与行政变迁，秦汉以来闽越土著与南楚、东吴汉人沿建溪、沙溪逐步开发定居，中古后历经多次中原南迁、赣东与闽北移民涌入，明代因沙县民变与永安设县，方言重心从沙县转向永安，最终定型为沙溪流域独有的闽中方言体系。作为福建 “方言博物馆” 的核心构成，三明话（闽中语）通行于三明市区、沙县、永安，长期被客家话（宁化、清流）、闽赣方言（建宁、泰宁）、闽南话（大田）、过渡方言（将乐、明溪、尤溪）环绕，形成 “一城多腔、隔山异音” 的复杂格局，既保留上古、中古汉语的六声调、鼻化韵、闭口韵等古音特征，又深度吸收客家、赣语、闽北、闽南的词汇与语音，呈现出鲜明的过渡性与混合性。它不仅是沙溪流域民众日常交流、宗族礼仪、庙会节庆的母语，更承载着闽中山区农耕文化、山林商贸与移民共生的历史记忆，融入地方歌谣、小吃文化（如沙县小吃方言标识）、民间叙事之中，同时因使用人口有限、地理封闭性强，成为闽语中最珍稀的 “活化石” 之一，如今虽受普通话普及与人口流动影响，但仍是三明地域文化的核心标识，也是研究方言接触、闽语演变与闽中多元文化交融的珍贵样本。',
    images: [
      '/culture/sanming1.jpg',
      '/culture/sanming2.jpg',
      '/culture/sanming3.jpg',
      '/culture/sanming4.jpg',
      '/culture/sanming5.jpg'
    ]
  },
  nanping: {
    name: '南平',
    category: '闽北语',
    description: '南平话属于闽北语，主要通行于南平市及其周边地区。南平话是闽语的重要分支，具有独特的语音系统。',
    distribution: '南平市区、建瓯、建阳、武夷山、邵武、光泽、顺昌、浦城、松溪、政和等',
    features: [
      '声母系统独特',
      '韵母丰富',
      '声调有 6-7 个',
      '保留古汉语特点',
      '受赣语影响'
    ],
    vocabulary: [
      { chinese: '你', dialect: '你', pinyin: 'nǐ' },
      { chinese: '他', dialect: '渠', pinyin: 'qú' },
      { chinese: '什么', dialect: '甚毛', pinyin: 'siáng-mó̤' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'sié-uáng' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'dó̤-siâ' },
      { chinese: '漂亮', dialect: '好睇', pinyin: 'hó̤-tā̤' }
    ],
    examples: [
      { dialect: '你有食饱未？', chinese: '你吃饱了吗？' },
      { dialect: '我卜去南平。', chinese: '我要去南平。' }
    ],
    culture:'南平话又称延平土官话，是福建境内罕见的官话方言岛，其形成源于明代洪武年间延平卫设立后，大量来自南直隶的驻军及家属迁入，使江淮官话在当地成为主流语言，并在此前历代北方移民的基础上最终定型，明清时期一度作为闽北通行的官话与文教语言；它主要通行于南平市延平区及西芹镇，被闽北话、闽中话等方言环绕，长期以来既保留了江淮官话的基本特征，又吸收了周边闽语的词汇与语音，形成独特的北音闽韵风格，如今使用人口不多且以中老年为主，属于濒危方言；南平话承载着中原军屯文化与闽北本土文化交融的印记，既保留古汉语入声等传统语音特征与明代官话词汇，又融合了南平南词、闽江商贸等本土文化内涵，既是研究明代官话与方言演变的珍贵活态样本，也是延平地区重要的文化身份符号，目前正面临普通话冲击，相关非遗保护与传承工作已逐步开展。',
    images: [
      '/culture/nanping1.jpg',
      '/culture/nanping2.jpg',
      '/culture/nanping3.jpg',
      '/culture/nanping4.jpg'
    ]
  },
  ningde: {
    name: '宁德',
    category: '闽东语',
    description: '宁德话属于闽东语，主要通行于宁德市及其周边地区。宁德话与福州话同属闽东语，但有一定差异。',
    distribution: '宁德市区、福安、福鼎、霞浦、古田、屏南、寿宁、周宁、柘荣等',
    features: [
      '声母系统独特',
      '韵母丰富',
      '声调有 7 个',
      '保留古汉语特点',
      '与福州话有差异但可互通'
    ],
    vocabulary: [
      { chinese: '你', dialect: '汝', pinyin: 'nṳ̄' },
      { chinese: '他', dialect: '伊', pinyin: 'ĭ' },
      { chinese: '什么', dialect: '甚毛', pinyin: 'sièng-mò̤' },
      { chinese: '吃饭', dialect: '食饭', pinyin: 'siĕh-uâng' },
      { chinese: '谢谢', dialect: '多谢', pinyin: 'dŏ̤h-siâ' },
      { chinese: '漂亮', dialect: '好看', pinyin: 'hō̤-káung' }
    ],
    examples: [
      { dialect: '汝今旦有食饭无？', chinese: '你今天吃饭了吗？' },
      { dialect: '我卜去宁德。', chinese: '我要去宁德。' }
    ],
    culture: '宁德话属于闽东语侯官片（北片），以福安音为代表，是闽东方言的重要分支，其形成源于闽越土著文化与历代中原汉人南迁的长期交融，自秦汉至唐宋，大批北方士族与军民经浙南、闽东北通道迁入，带来中古汉语语音与词汇，并与当地闽越原住民语言深度融合，逐步形成独具特色的闽东方言。作为宁德及闽东北沿海山区的通用方言，它通行于福安、宁德、福鼎、霞浦、柘荣、寿宁、周宁等地，受浙南吴语、闽北语及沿海海洋文化多重影响，语音硬朗、声调丰富，保留大量古汉语入声与古语词，同时兼具沿海商贸与山区农耕的语言特色。宁德话深深扎根于畲汉混居的多元文化环境，是地方民俗、妈祖信仰、宗族仪式、民间歌谣与戏曲的重要载体，也是闽东北民众身份认同的重要标志，随着近代海上贸易与移民活动，更成为连接海内外闽东侨胞的文化纽带，尽管如今受普通话普及影响，年轻一代使用有所减少，但仍是闽东北地域文化不可或缺的核心组成，也是研究闽语演变、浙闽方言接触的重要活化石。',
    images: [
      '/culture/ningde1.jpg',
      '/culture/ningde2.jpg',
      '/culture/ningde3.jpg',
      '/culture/ningde4.jpg'
    ]
  }
}

const cityInfo = computed(() => {
  const cityCode = route.params.cityCode
  return cityData[cityCode] || cityData.fuzhou
})

// 图片切换函数
const updateImagesPosition = () => {
  if (!imageContainerRef.value) return
  
  const wrapper = imageContainerRef.value.querySelector('.images-wrapper')
  const imageWidth = imageContainerRef.value.offsetWidth
  
  gsap.to(wrapper, {
    x: -currentImageIndex.value * imageWidth,
    duration: 0.5,
    ease: 'power2.inOut'
  })
}

const nextImage = () => {
  const images = cityInfo.value.images
  if (!images || images.length === 0) return
  
  currentImageIndex.value = (currentImageIndex.value + 1) % images.length
  updateImagesPosition()
}

const prevImage = () => {
  const images = cityInfo.value.images
  if (!images || images.length === 0) return
  
  currentImageIndex.value = (currentImageIndex.value - 1 + images.length) % images.length
  updateImagesPosition()
}

const goToImage = (index) => {
  currentImageIndex.value = index
  updateImagesPosition()
}

// 自动播放
const startAutoPlay = () => {
  const images = cityInfo.value.images
  if (!images || images.length <= 1) return
  
  autoPlayTimer = setInterval(() => {
    nextImage()
  }, 3000) // 3 秒切换一次
}

const stopAutoPlay = () => {
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer)
    autoPlayTimer = null
  }
}

const goBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  // 初始化图片位置
  updateImagesPosition()
  // 开始自动播放
  startAutoPlay()
  
  // 鼠标悬停时停止自动播放
  if (imageContainerRef.value) {
    imageContainerRef.value.addEventListener('mouseenter', stopAutoPlay)
    imageContainerRef.value.addEventListener('mouseleave', startAutoPlay)
  }
})

// 监听城市变化，重置图片索引
watch(() => route.params.cityCode, () => {
  currentImageIndex.value = 0
  updateImagesPosition()
  startAutoPlay()
})

onBeforeUnmount(() => {
  stopAutoPlay()
})
</script>

<style scoped>
.city-detail-container {
  min-height: 100vh;
  background: url('/background1.png') no-repeat center center;
  background-size: cover;
  padding: 20px;
}

.city-header {
  max-width: 1000px;
  margin: 0 auto 30px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  padding: 10px 20px;
  background: white;
  color: #667eea;
  border: none;
  border-radius: 5px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.back-btn:hover {
  transform: translateY(-2px);
}

.city-header h1 {
  color: white;
  font-size: 32px;
  flex: 1;
}

.city-content {
  max-width: 1000px;
  margin: 0 auto;
}

.city-info-card {
  background: white;
  border-radius: 10px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.city-info-card h2 {
  color: #333;
  font-size: 28px;
  margin-bottom: 10px;
}

.city-tag {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 30px;
}

.info-section {
  margin-bottom: 30px;
}

.info-section h3 {
  color: #667eea;
  font-size: 20px;
  margin-bottom: 15px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.info-section p {
  color: #666;
  line-height: 1.8;
  font-size: 16px;
}

.info-section ul {
  list-style: none;
  padding-left: 0;
}

.info-section li {
  color: #666;
  line-height: 2;
  font-size: 16px;
  padding-left: 20px;
  position: relative;
}

.info-section li::before {
  content: '•';
  color: #667eea;
  position: absolute;
  left: 0;
  font-weight: bold;
}

.vocabulary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 20px;
}

.vocabulary-item {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #e9ecef;
  transition: transform 0.3s, box-shadow 0.3s;
}

.vocabulary-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.word-chinese {
  color: #333;
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 14px;
}

.word-dialect {
  color: #667eea;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.word-pinyin {
  color: #999;
  font-size: 12px;
  font-style: italic;
}

.examples-container {
  margin-top: 20px;
}

.example-item {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  border-left: 4px solid #667eea;
}

.example-dialect {
  color: #667eea;
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 16px;
}

.example-chinese {
  color: #666;
  font-size: 14px;
}

.culture-section {
  margin-bottom: 30px;
}

.culture-section h3 {
  color: #667eea;
  font-size: 20px;
  margin-bottom: 15px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.culture-text {
  color: #666;
  line-height: 2;
  font-size: 15px;
  text-align: justify;
}

.culture-gallery {
  margin-top: 30px;
}

.culture-gallery h3 {
  color: #667eea;
  font-size: 20px;
  margin-bottom: 20px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.gallery-container {
  position: relative;
  width: 100%;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.images-wrapper {
  display: flex;
  transition: none;
}

.culture-image {
  width: 100%;
  min-width: 100%;
  height: 400px;
  object-fit: cover;
  display: block;
}

.gallery-nav {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.nav-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.nav-btn:hover {
  background: white;
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.indicators {
  display: flex;
  gap: 8px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid white;
}

.indicator:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.2);
}

.indicator.active {
  background: white;
  transform: scale(1.3);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}
</style>
